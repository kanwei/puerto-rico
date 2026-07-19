#!/usr/bin/env python3
"""Generational self-training loop for the Puerto Rico AlphaZero AI.

Each generation:
  1. self-play      -- generate games with the current champion (rollouts for gen 0)
  2. train          -- fit a candidate network on a sliding window of recent data
  3. evaluate       -- the candidate plays head-to-head vs the champion
  4. promote        -- if the candidate's win rate clears the gate, it becomes champion

Clojure handles self-play and evaluation (it can call the ONNX models directly);
Python handles training. State passes through files in data/ and models/.

Run from the repo root:
    python train/loop.py --generations 8 --games 100 --sims 200 --epochs 3

Speed notes (measured on this repo):
  - Gen 0 uses random rollouts (~240 ms/decision at 200 sims) - the slow gen.
    Keep --sims modest here; parallel self-play spreads it across all cores.
  - Gen 1+ use the network, which is ~7x faster per simulation (36 ms/decision
    at 200 sims, 137 ms at 800). So once a net exists you can afford the
    400-800 sims that make good policy targets for LESS wall-clock than gen 0.
  - Keep epochs LOW (2-5): tiny datasets + many epochs = memorization, not
    strategy. The fix is more GAMES per gen, not more epochs. Training uses a
    sliding window of the last 5 generations (the replay buffer).

Resume an interrupted run with --start-gen N (champion inferred from models/).
"""

import argparse
import glob
import json
import os
import re
import subprocess
import sys
import time

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(ROOT, "data")
MODELS = os.path.join(ROOT, "models")


def run(cmd, **kw):
    print(f"\n$ {' '.join(cmd)}", flush=True)
    return subprocess.run(cmd, cwd=ROOT, check=True, **kw)


def run_capture(cmd):
    print(f"\n$ {' '.join(cmd)}", flush=True)
    p = subprocess.run(cmd, cwd=ROOT, check=True, capture_output=True, text=True)
    sys.stdout.write(p.stdout)
    return p.stdout


def self_play(gen, champion, games, sims, players):
    out = os.path.join(DATA, f"gen{gen}.jsonl")
    if os.path.exists(out):
        os.remove(out)
    cmd = ["clj", "-M:selfplay", "generate",
           "--games", str(games), "--sims", str(sims),
           "--players", str(players), "--out", out]
    if champion:
        cmd += ["--model", champion]
    run(cmd)
    return out


def training_window(gen, window):
    """Concatenate the last `window` generations of data into one training file."""
    files = [os.path.join(DATA, f"gen{g}.jsonl")
             for g in range(max(1, gen - window + 1), gen + 1)]
    files = [f for f in files if os.path.exists(f)]
    win = os.path.join(DATA, "window.jsonl")
    with open(win, "w") as w:
        for f in files:
            with open(f) as r:
                w.write(r.read())
    return win


def train(gen, resume, epochs, batch, width, blocks):
    data = training_window(gen, window=5)
    out = os.path.join(MODELS, f"gen{gen}")
    # use the same interpreter running this loop (i.e. the venv python)
    cmd = [sys.executable, os.path.join(ROOT, "train", "train.py"),
           "--data", data, "--out", out,
           "--epochs", str(epochs), "--batch", str(batch),
           "--width", str(width), "--blocks", str(blocks)]
    if resume:
        cmd += ["--resume", resume]
    run(cmd)
    return out + ".onnx", out + ".pt"


def evaluate(challenger, champion, games, sims, players):
    cmd = ["clj", "-M:selfplay", "versus",
           "--challenger", challenger,
           "--games", str(games), "--sims", str(sims),
           "--players", str(players)]
    if champion:
        cmd += ["--champion", champion]
    out = run_capture(cmd)
    m = re.search(r"^RESULT (.+)$", out, re.MULTILINE)
    return json.loads(m.group(1)) if m else {"winrate": 0.0}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--generations", type=int, default=8)
    ap.add_argument("--games", type=int, default=100, help="self-play games per gen")
    ap.add_argument("--eval-games", type=int, default=30, help="head-to-head games")
    ap.add_argument("--sims", type=int, default=200)
    ap.add_argument("--eval-sims", type=int, default=100)
    ap.add_argument("--players", type=int, default=3)
    ap.add_argument("--epochs", type=int, default=3,
                    help="keep low (2-5); more games beats more epochs")
    ap.add_argument("--batch", type=int, default=256)
    ap.add_argument("--width", type=int, default=384)
    ap.add_argument("--blocks", type=int, default=5)
    ap.add_argument("--promote", type=float, default=None,
                    help="win-rate gate to become champion (default: baseline + 0.07)")
    ap.add_argument("--start-gen", type=int, default=1)
    args = ap.parse_args()

    os.makedirs(DATA, exist_ok=True)
    os.makedirs(MODELS, exist_ok=True)
    gate = args.promote if args.promote is not None else (1.0 / args.players) + 0.07

    # infer champion from a resumed run
    champion, champion_pt = None, None
    if args.start_gen > 1:
        for g in range(args.start_gen - 1, 0, -1):
            cand = os.path.join(MODELS, f"gen{g}.onnx")
            if os.path.exists(cand):
                champion, champion_pt = cand, cand[:-5] + ".pt"
                print(f"resuming with champion {champion}")
                break

    history = []
    for gen in range(args.start_gen, args.generations + 1):
        t0 = time.time()
        print(f"\n{'=' * 70}\nGENERATION {gen}  (champion: {champion or 'rollouts'})\n{'=' * 70}")

        self_play(gen, champion, args.games, args.sims, args.players)
        cand_onnx, cand_pt = train(gen, champion_pt, args.epochs,
                                   args.batch, args.width, args.blocks)
        result = evaluate(cand_onnx, champion, args.eval_games,
                          args.eval_sims, args.players)
        winrate = result["winrate"]

        promoted = (champion is None) or (winrate >= gate)
        if promoted:
            champion, champion_pt = cand_onnx, cand_pt
        history.append((gen, winrate, gate, promoted, time.time() - t0))
        print(f"\ngen {gen}: challenger winrate {winrate:.1%} vs gate {gate:.1%} "
              f"-> {'PROMOTED' if promoted else 'rejected'}  ({time.time() - t0:.0f}s)")

    print(f"\n{'=' * 70}\nSUMMARY\n{'=' * 70}")
    print(f"{'gen':>4} {'winrate':>9} {'gate':>7} {'result':>10} {'time':>7}")
    for gen, wr, gate, promoted, secs in history:
        print(f"{gen:>4} {wr:>8.1%} {gate:>6.1%} "
              f"{'PROMOTED' if promoted else 'rejected':>10} {secs:>6.0f}s")
    print(f"\nfinal champion: {champion}")


if __name__ == "__main__":
    main()
