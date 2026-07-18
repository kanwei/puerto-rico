#!/usr/bin/env python3
"""AlphaZero-style network for Puerto Rico.

Trains a residual MLP with a policy head (54 actions) and a value head
(one win-probability logit per player) on self-play data produced by
`clj -M:selfplay generate`.

Usage:
    python train.py --data ../data/selfplay.jsonl --out ../models/pr
    python train.py --data ../data/selfplay.jsonl --out ../models/pr \
        --epochs 20 --batch 512 --width 512 --blocks 6

Outputs <out>.pt (checkpoint) and <out>.onnx (for inference from Clojure
via onnxruntime).
"""

import argparse
import json
import math
import os
import time

import torch
import torch.nn as nn
from torch.utils.data import DataLoader, TensorDataset


def pick_device() -> torch.device:
    if torch.cuda.is_available():
        return torch.device("cuda")
    if torch.backends.mps.is_available():
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

    def forward(self, x):
        h = self.body(self.stem(x))
        return self.policy_head(h), self.value_head(h)  # raw logits


def load_data(path: str):
    states, policies, values = [], [], []
    with open(path) as f:
        for line in f:
            ex = json.loads(line)
            states.append(ex["s"])
            policies.append(ex["p"])
            values.append(ex["v"])
    return (torch.tensor(states, dtype=torch.float32),
            torch.tensor(policies, dtype=torch.float32),
            torch.tensor(values, dtype=torch.float32))


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
    ap.add_argument("--resume", help="checkpoint (.pt) to continue from")
    args = ap.parse_args()

    device = pick_device()
    states, policies, values = load_data(args.data)
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
    loader = DataLoader(TensorDataset(states, policies, values),
                        batch_size=args.batch, shuffle=True)

    for epoch in range(args.epochs):
        t0, p_sum, v_sum, batches = time.time(), 0.0, 0.0, 0
        for s, p, v in loader:
            s, p, v = s.to(device), p.to(device), v.to(device)
            p_logits, v_logits = net(s)
            p_loss = soft_cross_entropy(p_logits, p)
            v_loss = soft_cross_entropy(v_logits, v)
            loss = p_loss + v_loss
            opt.zero_grad()
            loss.backward()
            opt.step()
            p_sum += p_loss.item()
            v_sum += v_loss.item()
            batches += 1
        print(f"epoch {epoch + 1}/{args.epochs}  "
              f"policy_loss={p_sum / batches:.4f}  value_loss={v_sum / batches:.4f}  "
              f"({time.time() - t0:.1f}s)")

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
                      output_names=["policy_logits", "value_logits"],
                      dynamic_axes={"state": {0: "batch"},
                                    "policy_logits": {0: "batch"},
                                    "value_logits": {0: "batch"}})
    print(f"saved {args.out}.pt and {args.out}.onnx")


if __name__ == "__main__":
    main()
