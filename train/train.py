#!/usr/bin/env python3
"""AlphaZero-style network for Puerto Rico.

Trains a residual MLP with three heads on self-play data produced by
`clj -M:selfplay generate`:
  - policy head: one logit per action (softmax cross-entropy vs MCTS visits)
  - value head:  one logit per player, win probability (cross-entropy vs outcome)
  - score head:  one raw output per player = predicted SCORE MARGIN (final score
                 minus table average, in tens of points). MSE against sm/10.
                 A zero-centered, dense signal that also drives the MCTS utility
                 blend (keep maximizing point lead even when the win is decided).

Usage:
    python train.py --data ../data/p3-window-328-98.bin --out ../models/p3-gen1
    python train.py --data ../data/p3-window-328-98.bin --out ../models/p3-gen1 \
        --epochs 20 --batch 512 --width 512 --blocks 6

Data is a raw little-endian float32 matrix written by `clj -M:selfplay generate`
(see load_data); the row layout is read straight from the filename shape suffix.

Outputs <out>.pt (checkpoint) and <out>.onnx (for inference from Clojure
via onnxruntime).
"""

import argparse
import math
import os
import re
import time

import numpy as np
import torch
import torch.nn as nn
from torch.utils.data import DataLoader, TensorDataset


def pick_device(pref: str = "auto") -> torch.device:
    # "cuda" also covers AMD ROCm builds of torch (they present as cuda/HIP).
    if pref != "auto":
        return torch.device(pref)
    if torch.cuda.is_available():
        return torch.device("cuda")
    if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
        return torch.device("mps")
    return torch.device("cpu")


class ResBlock(nn.Module):
    def __init__(self, width: int):
        super().__init__()
        self.l1 = nn.Linear(width, width)
        self.n1 = nn.LayerNorm(width)
        self.l2 = nn.Linear(width, width)
        self.n2 = nn.LayerNorm(width)

    def forward(self, x):
        h = torch.relu(self.n1(self.l1(x)))
        h = self.n2(self.l2(h))
        return torch.relu(x + h)


class PuertoRicoNet(nn.Module):
    def __init__(self, in_dim: int, n_actions: int, n_players: int,
                 width: int = 512, blocks: int = 6):
        super().__init__()
        self.stem = nn.Sequential(nn.Linear(in_dim, width),
                                  nn.LayerNorm(width), nn.ReLU())
        self.body = nn.Sequential(*[ResBlock(width) for _ in range(blocks)])
        self.policy_head = nn.Linear(width, n_actions)
        self.value_head = nn.Linear(width, n_players)
        self.score_head = nn.Linear(width, n_players)

    def forward(self, x):
        h = self.body(self.stem(x))
        # policy & value are logits; score margins are a raw (signed) regression
        return (self.policy_head(h),
                self.value_head(h),
                self.score_head(h))


# score margins are stored in points; scale down so they don't dominate grads
SCORE_SCALE = 10.0


def parse_shape(path: str):
    """(players N, state floats S, action floats A) from a p<N>-...-<S>-<A>.bin name."""
    m = re.search(r"p(\d+)-.*?-(\d+)-(\d+)\.bin$", os.path.basename(path))
    if not m:
        raise ValueError(
            f"cannot parse shape from '{path}'. Expected a name like "
            "p3-window-328-98.bin (p<players>-...-<state>-<actions>.bin).")
    return int(m.group(1)), int(m.group(2)), int(m.group(3))


def load_data(path: str):
    """Read the raw float32 matrix and slice each row into state/policy/value/sm.
    Row layout: [state S | policy A | value N | score-margin N], N = players."""
    n_players, s_dim, a_dim = parse_shape(path)
    stride = s_dim + a_dim + 2 * n_players
    arr = np.fromfile(path, dtype="<f4")
    if arr.size == 0 or arr.size % stride != 0:
        raise ValueError(
            f"{path}: {arr.size} floats is not a whole number of rows of "
            f"stride {stride} (S={s_dim} A={a_dim} players={n_players}).")
    rows = arr.reshape(-1, stride)
    s = rows[:, :s_dim]
    p = rows[:, s_dim:s_dim + a_dim]
    v = rows[:, s_dim + a_dim:s_dim + a_dim + n_players]
    # zero-centered margins, scaled into "tens of points"
    sm = rows[:, s_dim + a_dim + n_players:] / SCORE_SCALE
    tens = lambda a: torch.from_numpy(np.ascontiguousarray(a)).float()
    return tens(s), tens(p), tens(v), tens(sm)


def soft_cross_entropy(logits, target):
    return -(target * torch.log_softmax(logits, dim=1)).sum(dim=1).mean()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", required=True)
    ap.add_argument("--out", default="models/pr")
    ap.add_argument("--epochs", type=int, default=10)
    ap.add_argument("--batch", type=int, default=256)
    ap.add_argument("--lr", type=float, default=1e-3)
    ap.add_argument("--weight-decay", type=float, default=1e-4)
    ap.add_argument("--width", type=int, default=512)
    ap.add_argument("--blocks", type=int, default=6)
    ap.add_argument("--score-weight", type=float, default=0.5,
                    help="weight of the auxiliary score-margin MSE loss")
    ap.add_argument("--resume", help="checkpoint (.pt) to continue from")
    ap.add_argument("--device", default="auto", choices=["auto", "cpu", "cuda", "mps"],
                    help="'cuda' also selects AMD ROCm; 'auto' picks cuda/mps/cpu")
    args = ap.parse_args()

    device = pick_device(args.device)
    states, policies, values, margins = load_data(args.data)
    in_dim, n_actions, n_players = states.shape[1], policies.shape[1], values.shape[1]
    print(f"device={device}  examples={len(states)}  "
          f"in_dim={in_dim}  actions={n_actions}  players={n_players}")

    net = PuertoRicoNet(in_dim, n_actions, n_players,
                        width=args.width, blocks=args.blocks).to(device)
    if args.resume:
        net.load_state_dict(torch.load(args.resume, map_location=device)["model"])
        print(f"resumed from {args.resume}")

    opt = torch.optim.AdamW(net.parameters(), lr=args.lr,
                            weight_decay=args.weight_decay)
    loader = DataLoader(TensorDataset(states, policies, values, margins),
                        batch_size=args.batch, shuffle=True)
    mse = nn.MSELoss()

    for epoch in range(args.epochs):
        t0, p_sum, v_sum, m_sum, batches = time.time(), 0.0, 0.0, 0.0, 0
        for s, p, v, sm in loader:
            s, p, v, sm = s.to(device), p.to(device), v.to(device), sm.to(device)
            p_logits, v_logits, pred_margins = net(s)
            p_loss = soft_cross_entropy(p_logits, p)
            v_loss = soft_cross_entropy(v_logits, v)
            m_loss = mse(pred_margins, sm)
            loss = p_loss + v_loss + args.score_weight * m_loss
            opt.zero_grad()
            loss.backward()
            opt.step()
            p_sum += p_loss.item()
            v_sum += v_loss.item()
            m_sum += m_loss.item()
            batches += 1
        print(f"epoch {epoch + 1}/{args.epochs}  "
              f"policy_loss={p_sum / batches:.4f}  value_loss={v_sum / batches:.4f}  "
              f"margin_loss={m_sum / batches:.4f}  ({time.time() - t0:.1f}s)")

    os.makedirs(os.path.dirname(args.out) or ".", exist_ok=True)
    torch.save({"model": net.state_dict(),
                "in_dim": in_dim, "n_actions": n_actions,
                "n_players": n_players,
                "width": args.width, "blocks": args.blocks},
               args.out + ".pt")

    net.eval().to("cpu")
    dummy = torch.zeros(1, in_dim)
    torch.onnx.export(net, dummy, args.out + ".onnx",
                      input_names=["state"],
                      output_names=["policy_logits", "value_logits", "score_margins"],
                      dynamic_axes={"state": {0: "batch"},
                                    "policy_logits": {0: "batch"},
                                    "value_logits": {0: "batch"},
                                    "score_margins": {0: "batch"}})
    print(f"saved {args.out}.pt and {args.out}.onnx")


if __name__ == "__main__":
    main()
