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
import shutil
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
    """Run a command, echo its output, and return (stdout, returncode).
    Does NOT raise on non-zero exit - callers decide (e.g. a versus run may
    print its RESULT and still exit non-zero from a benign native shutdown)."""
    print(f"\n$ {' '.join(cmd)}", flush=True)
    p = subprocess.run(cmd, cwd=ROOT, capture_output=True, text=True)
    sys.stdout.write(p.stdout)
    if p.returncode != 0:
        # surface the real error instead of a bare CalledProcessError traceback
        sys.stderr.write(p.stderr)
    return p.stdout, p.returncode


def self_play(gen, champion, games, sims, players, utility_c, rollout):
    # Clojure appends a `-<S>-<A>.bin` shape suffix, so pass a base name and
    # glob for the actual file it wrote.
    base = os.path.join(DATA, f"p{players}-gen{gen}")
    for f in glob.glob(base + "-*.bin"):
        os.remove(f)
    if gen == 1:
        games = games // 10
    cmd = ["clj", "-M:selfplay", "generate",
           "--games", str(games), "--sims", str(sims),
           "--players", str(players), "--out", base + ".bin"]
    if champion:
        # utility-c only affects NN search, so pass it only when a model drives
        # self-play (the rollout generation ignores it)
        cmd += ["--model", champion, "--utility-c", str(utility_c)]
    else:
        # gen-0 bootstrap: heuristic playouts give the value head a real signal
        # (random playouts can't tell that taking plantations / building helps)
        cmd += ["--rollout", rollout]
    run(cmd)
    files = glob.glob(base + "-*.bin")
    if not files:
        raise RuntimeError(f"self-play produced no data file for {base}")
    return files[0]


def training_window(gen, window, players):
    """Combine the last `window` generations of data into one training file by
    plain byte concatenation - every gen for this player count shares one row
    stride, so the concatenated float32 stream is still a valid matrix."""
    files = []
    for g in range(max(1, gen - window + 1), gen + 1):
        files += glob.glob(os.path.join(DATA, f"p{players}-gen{g}-*-*.bin"))
    files = sorted(files)
    if not files:
        raise RuntimeError(f"no self-play data files for {players}p window")
    # carry the shape suffix onto the window file so train.py can parse it
    m = re.search(r"-(\d+-\d+)\.bin$", os.path.basename(files[0]))
    win = os.path.join(DATA, f"p{players}-window-{m.group(1)}.bin")
    with open(win, "wb") as w:
        for f in files:
            with open(f, "rb") as r:
                shutil.copyfileobj(r, w)
    return win


def train(gen, resume, epochs, batch, width, blocks, device, players, arch,
          d_model, layers, heads):
    data = training_window(gen, 5, players)
    out = os.path.join(MODELS, f"p{players}-gen{gen}")
    # use the same interpreter running this loop (i.e. the venv python)
    cmd = [sys.executable, os.path.join(ROOT, "train", "train.py"),
           "--data", data, "--out", out,
           "--epochs", str(epochs), "--batch", str(batch),
           "--arch", arch,
           "--width", str(width), "--blocks", str(blocks),
           "--d-model", str(d_model), "--layers", str(layers),
           "--heads", str(heads),
           "--device", device]
    if resume:
        cmd += ["--resume", resume]
    run(cmd)
    return out + ".onnx", out + ".pt"


def evaluate(challenger, champion, games, sims, players, utility_c):
    cmd = ["clj", "-M:selfplay", "versus",
           "--challenger", challenger,
           "--games", str(games), "--sims", str(sims),
           "--players", str(players),
           "--utility-c", str(utility_c)]
    if champion:
        cmd += ["--champion", champion]
    out, code = run_capture(cmd)
    m = re.search(r"^RESULT (.+)$", out, re.MULTILINE)
    if m:
        # trust a printed RESULT even if the JVM exited non-zero on shutdown
        return json.loads(m.group(1))
    raise RuntimeError(
        f"evaluation produced no RESULT (clj exited {code}). See the error above "
        f"- a common cause is a model/game player-count mismatch.")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--generations", type=int, default=10)
    ap.add_argument("--games", type=int, default=1000, help="self-play games per gen")
    ap.add_argument("--eval-games", type=int, default=30, help="head-to-head games")
    ap.add_argument("--sims", type=int, default=200)
    ap.add_argument("--eval-sims", type=int, default=100)
    ap.add_argument("--players", type=int, default=3)
    ap.add_argument("--epochs", type=int, default=3,
                    help="keep low (2-5); more games beats more epochs")
    ap.add_argument("--batch", type=int, default=256)
    ap.add_argument("--arch", default="mlp", choices=["mlp", "transformer"],
                    help="network architecture for every generation")
    ap.add_argument("--width", type=int, default=384, help="mlp hidden width")
    ap.add_argument("--blocks", type=int, default=5, help="mlp residual blocks")
    ap.add_argument("--d-model", type=int, default=128, help="transformer dim")
    ap.add_argument("--layers", type=int, default=3, help="transformer layers")
    ap.add_argument("--heads", type=int, default=4, help="transformer heads")
    ap.add_argument("--utility-c", type=float, default=0.5,
                    help="MCTS score-margin weight for NN search (U = win + c*margin). "
                         "Higher lets the reliable score-margin head drive search instead "
                         "of the noisier win/loss head; 0.05 is the old default.")
    ap.add_argument("--rollout", default="heuristic", choices=["heuristic", "random"],
                    help="gen-0 playout policy. heuristic (default) gives the value "
                         "head a real long-horizon signal; random is the old behavior.")
    ap.add_argument("--promote", type=float, default=None,
                    help="win-rate gate to become champion (default: baseline + 0.07)")
    ap.add_argument("--start-gen", type=int, default=1)
    ap.add_argument("--device", default="auto", choices=["auto", "cpu", "cuda", "mps"],
                    help="training device; 'cuda' also selects AMD ROCm")
    args = ap.parse_args()

    os.makedirs(DATA, exist_ok=True)
    os.makedirs(MODELS, exist_ok=True)
    gate = args.promote if args.promote is not None else (1.0 / args.players) + 0.07

    # infer champion from a resumed run
    champion, champion_pt = None, None
    if args.start_gen > 1:
        for g in range(args.start_gen - 1, 0, -1):
            cand = os.path.join(MODELS, f"p{args.players}-gen{g}.onnx")
            if os.path.exists(cand):
                champion, champion_pt = cand, cand[:-5] + ".pt"
                print(f"resuming with champion {champion}")
                break

    history = []
    for gen in range(args.start_gen, args.generations + 1):
        t0 = time.time()
        print(f"\n{'=' * 70}\nGENERATION {gen}  (champion: {champion or 'rollouts'})\n{'=' * 70}")

        self_play(gen, champion, args.games, args.sims, args.players,
                  args.utility_c, args.rollout)
        cand_onnx, cand_pt = train(gen, champion_pt, args.epochs,
                                   args.batch, args.width, args.blocks, args.device,
                                   args.players, args.arch, args.d_model,
                                   args.layers, args.heads)
        result = evaluate(cand_onnx, champion, args.eval_games,
                          args.eval_sims, args.players, args.utility_c)
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
