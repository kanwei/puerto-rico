# Training the Puerto Rico AI on another machine

The self-training pipeline has two halves that run on the **same machine**:

| Stage | Runs on | Uses |
|-------|---------|------|
| Self-play data generation | JVM (Clojure) | all CPU cores |
| Head-to-head evaluation   | JVM (Clojure) | all CPU cores |
| Network training          | Python (PyTorch) | CPU (tiny net); GPU optional |

`train/loop.py` orchestrates all three, shelling out to `clj` for self-play/eval
and to the venv Python for training.

**What actually matters for this project: CPU cores, not the GPU.** The network
is tiny (~328 inputs, 256–384 wide), so training is trivially fast even on CPU
(seconds per epoch). The real bottleneck is self-play — CPU-bound MCTS — which
scales with core count. So a good training box is simply one with **many CPU
cores**. A GPU helps training a little but is optional and never the limiter
here.

Everything is driven through `bb` (Babashka) tasks — see `bb tasks`.

---

## 1. Prerequisites on the Linux workstation

- **JDK 21+** and the **Clojure CLI** (`clj`) — for self-play and evaluation.
  ```bash
  # Clojure CLI (see https://clojure.org/guides/install_clojure)
  curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
  chmod +x linux-install.sh && sudo ./linux-install.sh
  ```
- **Babashka** (`bb`) — task runner.
  ```bash
  bash <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)
  ```
- **Python 3.10+** with the `venv` module. On Debian/Ubuntu the venv module is a
  separate package:
  ```bash
  sudo apt install python3 python3-venv python3-pip
  ```
- **A GPU is optional** (see the device section below). CPU training is the
  recommended default here — no accelerator setup required.

Then get the repo onto the box (git clone, rsync, scp — whatever you use).

---

## 2. One-time Python setup

From the repo root:

```bash
bb venv
```

This creates `train/.venv`, upgrades pip, installs `train/requirements.txt`
(torch, onnx, onnxscript), and prints a device check, e.g.:

```
torch 2.x.x
cuda available: False
mps available: False
```

### Choosing a training device

`train.py` auto-selects `cuda → mps → cpu`, or you can force it with
`--device {auto,cpu,cuda,mps}` (also on `bb loop --device …`). "cuda" also
covers AMD ROCm builds of torch (they present themselves as `cuda`/HIP).

**AMD Ryzen AI Max+ 395 (Strix Halo) — recommended: just use the CPU.** It has
16 Zen 5 cores, which is exactly what self-play wants, and the network is so
small that CPU training is a rounding error next to self-play time. The default
CPU torch wheel from `bb venv` works out of the box; `cuda available: False` is
expected and fine. Nothing else to install.

**Optional: the Radeon 8060S iGPU via ROCm (Linux only).** Recent ROCm (≈6.4+)
added support for this GPU (gfx1151). If you want to try it, install the ROCm
torch wheel and, because the iGPU isn't an officially blessed target, you may
need a gfx-version override:

```bash
train/.venv/bin/pip install --force-reinstall torch \
    --index-url https://download.pytorch.org/whl/rocm6.2
HSA_OVERRIDE_GFX_VERSION=11.0.0 train/.venv/bin/python -c "import torch; print(torch.cuda.is_available())"
```

Honestly, for a network this size it isn't worth the fuss — the payoff over CPU
is small and self-play (CPU) dominates the wall-clock either way. If ROCm
auto-detect ever misbehaves, force CPU with `--device cpu`.

(On a Mac, `mps available: True` and training uses the Apple GPU — same code.)

---

## 3. Run the training loop

```bash
# Full generational run: gen 0 from rollouts, then gen 1..N each trained on a
# 5-generation replay window and promoted only if it beats the champion.
bb loop --generations 8 --games 500 --sims 200
```

Useful knobs (`bb loop --help` for all):

- `--games N`      self-play games per generation (more games ≫ more epochs)
- `--sims N`       MCTS simulations per move during self-play
- `--eval-games N` head-to-head games when testing a new gen vs the champion
- `--epochs N`     training epochs — **keep 2–5**; tiny data + many epochs overfits
- `--players N`    3–5
- `--width / --blocks`  network size (default 384 / 5; drop to 256 / 4 to go faster)
- `--start-gen N`  resume an interrupted run (champion is inferred from `models/`)

**Speed guidance:** gen 0 uses random rollouts (~240 ms/decision at 200 sims) and
is the slow one — keep its `--sims` modest, parallelism spreads it over all
cores. From gen 1 the network drives MCTS (~7× faster per sim), so you can raise
`--sims` to 400–800 for better policy targets without paying more wall-clock.

### Running a single stage

```bash
# generate data (JVM). Pass a base --out; Clojure appends a -<S>-<A>.bin shape
# suffix, e.g. data/p3-gen1-328-98.bin
bb selfplay --games 200 --sims 200 --players 3 --out data/p3-gen1.bin
bb train    --data data/p3-gen1-328-98.bin --out models/p3-gen1 --epochs 3
bb versus   --challenger models/p3-gen2.onnx --champion models/p3-gen1.onnx --games 40
bb arena    --games 20 --sims 150                          # MCTS vs heuristic
```

---

## 4. Outputs and using the trained model

- `data/p<N>-gen<G>-<S>-<A>.bin`  self-play datasets: raw little-endian float32
  matrix, rows of [state S | policy A | value N | margin N] for N players
  (combine gens by plain byte concat) (one file per gen, gitignored)
- `models/p<N>-gen<G>.onnx` + `.pt`  each generation's network for N players (gitignored)

`train.py` writes both a PyTorch checkpoint (`.pt`, for `--resume`) and an ONNX
export (`.onnx`, plus an `.onnx.data` weights sidecar). The **ONNX** file is what
the game engine loads for play and evaluation.

To play against a trained model: copy `models/genN.onnx` **and** its
`models/genN.onnx.data` sidecar into the `models/` directory of the machine
running the game server (`clj -M:run-m`). It then appears automatically in the
new-game setup screen's per-seat bot dropdown.

---

## 5. Notes

- **A model is locked to one player count.** The egocentric state encoding grows
  with the number of players (3p → 328 inputs, 4p → 398, 5p → 468), so a model's
  input width is fixed at whatever it trained on. Using a 3-player model in a
  4-player game (or feeding a stale model to the game/`versus`) now fails with a
  clear message ("Model … expects 328 inputs but this 4-player state encodes to
  398 …"). Keep a whole training run at ONE `--players` value, and don't
  `--start-gen` resume across different player counts. To support another player
  count, run a separate loop for it (models are named per generation, so use a
  different `models/` dir or move them aside first).
- Self-play, evaluation, and the game all share one code path
  (`src/cljc/puerto_rico/ai/…`), so a model trained here plays identically on the
  Mac. Just keep the code in sync — **any change to the state encoder or action
  space invalidates existing models** (the input/output dimensions change);
  regenerate data and retrain from gen 0.
- `train/loop.py` calls the venv Python via `sys.executable`, so as long as you
  launch it through `bb loop` (or `train/.venv/bin/python train/loop.py`), the
  training subprocess uses the venv automatically.
