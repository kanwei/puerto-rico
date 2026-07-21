#!/usr/bin/env python3
"""AlphaZero-style network for Puerto Rico.

Trains a network with three heads on self-play data produced by
`clj -M:selfplay generate`. Two architectures (`--arch`):
  - mlp:         a residual MLP over the flat state vector (default, fast)
  - transformer: a fat-token transformer encoder (fewer, denser tokens per
                 player; better at relational reasoning, a bit slower)
Both export to the same ONNX input/output signature, so inference (nn.clj) is
identical regardless of architecture.

The three heads:
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
    """Residual MLP over the flat state vector (the original architecture)."""

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


# ---------------------------------------------------------------------------
# Transformer architecture ("fat tokens")
#
# The flat state vector is a concatenation of fixed-size blocks (see
# encoder.cljc). Rather than one token per building - which forces the GPU to
# attend over the many empty [0,0] building slots a player doesn't own - we
# group each player's data into 3 "fat tokens" (inventory, plantations,
# buildings) plus a few global tokens (roles, ships, everything else). That's
# 3*n_players + 3 tokens total (12 for 3p), so attention is cheap.
#
# The slice offsets are derived HERE from the same layout the Clojure encoder
# emits, and asserted to sum to in_dim, so any encoder change surfaces as a
# loud mismatch rather than silently misaligned tokens.
# ---------------------------------------------------------------------------

def _num_cargo_ships(n: int) -> int:
    # mirrors state/num-cargo-ships: the 2-player variant has only 2 ships
    return 2 if n == 2 else 3


# per-player block = 67 floats, in this order (encoder.cljc encode-player!)
PLAYER_SEGMENTS = [("inventory", 9), ("plantations", 12), ("buildings", 46)]
PLAYER_BLOCK = sum(size for _, size in PLAYER_SEGMENTS)  # 67


def _global_segments(n: int):
    """Global block layout (encoder.cljc encode-global!), in emission order."""
    ships = _num_cargo_ships(n)
    return [
        ("decision", 9), ("roles", 24), ("gov", n), ("selector", n + 1),
        ("plant_display", 5), ("quarry", 1), ("plant_supply", 1),
        ("colonist_ship", 1), ("colonist_supply", 1), ("vp_supply", 1),
        ("final_round", 1), ("building_supply", 23), ("trading_house", 6),
        ("ships", 8 * ships), ("captain", 2 + n), ("hacienda", 1),
        ("storage", 11), ("privilege", 5),
    ]


class StateTokenizer(nn.Module):
    """Slice the flat state into fat tokens and project each to d_model.

    Per-player projections are SHARED across seats (the encoding is egocentric,
    so seat k means the same thing for every player) and distinguished by a
    learned per-seat embedding, matching the encoder's weight-sharing design.
    """

    def __init__(self, n_players: int, in_dim: int, d_model: int):
        super().__init__()
        self.n_players = n_players
        ships = _num_cargo_ships(n_players)

        # absolute [start, end) ranges for each segment, derived from the layout
        self.player_ranges = []
        for p in range(n_players):
            o = p * PLAYER_BLOCK
            segs = {}
            for name, size in PLAYER_SEGMENTS:
                segs[name] = (o, o + size)
                o += size
            self.player_ranges.append(segs)

        o = n_players * PLAYER_BLOCK
        gseg = {}
        for name, size in _global_segments(n_players):
            gseg[name] = (o, o + size)
            o += size
        assert o == in_dim, (
            f"tokenizer layout sums to {o} but in_dim is {in_dim}; the Python "
            f"layout is out of sync with encoder.cljc")

        self.roles_range = gseg["roles"]
        self.ships_range = gseg["ships"]
        # one "global" fat token = everything else in the global block, in order
        self.misc_ranges = [rng for name, rng in gseg.items()
                            if name not in ("roles", "ships")]
        misc_dim = sum(b - a for a, b in self.misc_ranges)

        self.proj_inventory = nn.Linear(PLAYER_SEGMENTS[0][1], d_model)
        self.proj_plantations = nn.Linear(PLAYER_SEGMENTS[1][1], d_model)
        self.proj_buildings = nn.Linear(PLAYER_SEGMENTS[2][1], d_model)
        self.proj_roles = nn.Linear(24, d_model)
        self.proj_ships = nn.Linear(8 * ships, d_model)
        self.proj_global = nn.Linear(misc_dim, d_model)

        self.emb_player = nn.Parameter(torch.randn(n_players, d_model) * 0.02)
        # token types: inventory, plantations, buildings, roles, ships, global
        self.emb_type = nn.Parameter(torch.randn(6, d_model) * 0.02)

    def forward(self, x):
        toks = []
        for p in range(self.n_players):
            r = self.player_ranges[p]
            a, b = r["inventory"]
            toks.append(self.proj_inventory(x[:, a:b])
                        + self.emb_player[p] + self.emb_type[0])
            a, b = r["plantations"]
            toks.append(self.proj_plantations(x[:, a:b])
                        + self.emb_player[p] + self.emb_type[1])
            a, b = r["buildings"]
            toks.append(self.proj_buildings(x[:, a:b])
                        + self.emb_player[p] + self.emb_type[2])
        a, b = self.roles_range
        toks.append(self.proj_roles(x[:, a:b]) + self.emb_type[3])
        a, b = self.ships_range
        toks.append(self.proj_ships(x[:, a:b]) + self.emb_type[4])
        misc = torch.cat([x[:, a:b] for a, b in self.misc_ranges], dim=1)
        toks.append(self.proj_global(misc) + self.emb_type[5])
        return torch.stack(toks, dim=1)  # [B, T, d_model]


class PuertoRicoTransformer(nn.Module):
    """Transformer encoder over fat tokens with a CLS token feeding the heads."""

    def __init__(self, in_dim: int, n_actions: int, n_players: int,
                 d_model: int = 128, layers: int = 3, heads: int = 4,
                 ff_mult: int = 4):
        super().__init__()
        self.tokenizer = StateTokenizer(n_players, in_dim, d_model)
        self.cls = nn.Parameter(torch.randn(1, 1, d_model) * 0.02)
        # norm_first=True keeps PyTorch off the BetterTransformer fast path, which
        # does not export cleanly to ONNX; the standard path does.
        enc_layer = nn.TransformerEncoderLayer(
            d_model, heads, dim_feedforward=ff_mult * d_model, dropout=0.0,
            activation="gelu", batch_first=True, norm_first=True)
        self.encoder = nn.TransformerEncoder(enc_layer, layers,
                                             enable_nested_tensor=False)
        self.norm = nn.LayerNorm(d_model)
        self.policy_head = nn.Linear(d_model, n_actions)
        self.value_head = nn.Linear(d_model, n_players)
        self.score_head = nn.Linear(d_model, n_players)

    def forward(self, x):
        toks = self.tokenizer(x)
        cls = self.cls.expand(x.shape[0], -1, -1)
        h = self.encoder(torch.cat([cls, toks], dim=1))
        h = self.norm(h[:, 0])  # CLS summary
        return (self.policy_head(h),
                self.value_head(h),
                self.score_head(h))


def build_net(arch: str, in_dim: int, n_actions: int, n_players: int, args):
    if arch == "mlp":
        return PuertoRicoNet(in_dim, n_actions, n_players,
                             width=args.width, blocks=args.blocks)
    if arch == "transformer":
        return PuertoRicoTransformer(in_dim, n_actions, n_players,
                                     d_model=args.d_model, layers=args.layers,
                                     heads=args.heads)
    raise ValueError(f"unknown --arch {arch!r} (expected mlp or transformer)")


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
    # Policy-loss mask: a forced move (a single legal action) has a one-hot
    # visit target and carries NO policy information - the move wasn't chosen,
    # it was the only option. ~23% of rows are forced, most of them "pass/done",
    # so training the policy head on them just biases it toward passing. Mask
    # those rows OUT of the policy loss (value/margin still train on them).
    pmask = (p.max(axis=1) < 0.999).astype("float32")
    tens = lambda a: torch.from_numpy(np.ascontiguousarray(a)).float()
    return tens(s), tens(p), tens(v), tens(sm), tens(pmask)


def masked_soft_cross_entropy(logits, target, mask):
    """Soft cross-entropy averaged over only the rows where mask==1."""
    per_row = -(target * torch.log_softmax(logits, dim=1)).sum(dim=1)
    return (per_row * mask).sum() / mask.sum().clamp(min=1.0)


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
    ap.add_argument("--arch", default="mlp", choices=["mlp", "transformer"],
                    help="network architecture")
    ap.add_argument("--width", type=int, default=512, help="mlp: hidden width")
    ap.add_argument("--blocks", type=int, default=6, help="mlp: residual blocks")
    ap.add_argument("--d-model", type=int, default=128,
                    help="transformer: token/model dimension")
    ap.add_argument("--layers", type=int, default=3,
                    help="transformer: encoder layers")
    ap.add_argument("--heads", type=int, default=4,
                    help="transformer: attention heads")
    ap.add_argument("--score-weight", type=float, default=0.5,
                    help="weight of the auxiliary score-margin MSE loss")
    ap.add_argument("--resume", help="checkpoint (.pt) to continue from")
    ap.add_argument("--device", default="auto", choices=["auto", "cpu", "cuda", "mps"],
                    help="'cuda' also selects AMD ROCm; 'auto' picks cuda/mps/cpu")
    args = ap.parse_args()

    device = pick_device(args.device)
    states, policies, values, margins, pmask = load_data(args.data)
    in_dim, n_actions, n_players = states.shape[1], policies.shape[1], values.shape[1]
    print(f"device={device}  examples={len(states)}  "
          f"in_dim={in_dim}  actions={n_actions}  players={n_players}  "
          f"policy-rows={int(pmask.sum())}/{len(pmask)} "
          f"({1 - pmask.mean().item():.0%} forced, masked out)")

    print(f"arch={args.arch}")
    net = build_net(args.arch, in_dim, n_actions, n_players, args).to(device)
    if args.resume:
        ckpt = torch.load(args.resume, map_location=device)
        if ckpt.get("arch", "mlp") != args.arch:
            raise ValueError(
                f"--resume checkpoint is arch {ckpt.get('arch', 'mlp')!r} but "
                f"--arch is {args.arch!r}; they must match")
        net.load_state_dict(ckpt["model"])
        print(f"resumed from {args.resume}")

    opt = torch.optim.AdamW(net.parameters(), lr=args.lr,
                            weight_decay=args.weight_decay)
    loader = DataLoader(TensorDataset(states, policies, values, margins, pmask),
                        batch_size=args.batch, shuffle=True)
    mse = nn.MSELoss()

    for epoch in range(args.epochs):
        t0, p_sum, v_sum, m_sum, batches = time.time(), 0.0, 0.0, 0.0, 0
        for s, p, v, sm, pm in loader:
            s, p, v, sm, pm = (s.to(device), p.to(device), v.to(device),
                               sm.to(device), pm.to(device))
            p_logits, v_logits, pred_margins = net(s)
            # policy trains only on real (multi-option) decisions; value and
            # margin train on every row
            p_loss = masked_soft_cross_entropy(p_logits, p, pm)
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
                "arch": args.arch,
                "in_dim": in_dim, "n_actions": n_actions,
                "n_players": n_players,
                "width": args.width, "blocks": args.blocks,
                "d_model": args.d_model, "layers": args.layers, "heads": args.heads},
               args.out + ".pt")

    net.eval().to("cpu")
    # Inference is one state at a time (MCTS leaf eval), so a batch-1 dummy is
    # fine. opset 18 is what the current exporter targets natively (17 triggers a
    # lossy automatic downconvert).
    dummy = torch.zeros(1, in_dim)
    torch.onnx.export(net, dummy, args.out + ".onnx",
                      input_names=["state"],
                      output_names=["policy_logits", "value_logits", "score_margins"],
                      opset_version=18,
                      dynamic_axes={"state": {0: "batch"},
                                    "policy_logits": {0: "batch"},
                                    "value_logits": {0: "batch"},
                                    "score_margins": {0: "batch"}})
    print(f"saved {args.out}.pt and {args.out}.onnx")


if __name__ == "__main__":
    main()
