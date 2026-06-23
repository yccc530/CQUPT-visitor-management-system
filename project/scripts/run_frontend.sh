#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR/frontend"

if ! command -v npm >/dev/null 2>&1; then
  echo "Node.js/npm is not available. Install Node.js LTS and reopen the terminal." >&2
  exit 1
fi

if ! command -v node >/dev/null 2>&1; then
  echo "Node.js is not available. Install Node.js LTS and reopen the terminal." >&2
  exit 1
fi

if [ ! -d node_modules ]; then
  npm install
fi

exec node node_modules/vite/bin/vite.js --host 0.0.0.0 --port 5173
