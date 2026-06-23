#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

ensure_ports_free() {
  if command -v powershell.exe >/dev/null 2>&1; then
    if ! powershell.exe -NoProfile -Command 'if (Get-NetTCPConnection -LocalPort 8080,5173 -State Listen -ErrorAction SilentlyContinue) { exit 1 }' >/dev/null 2>&1; then
      echo "Port 8080 or 5173 is already in use. Please stop the existing service first." >&2
      exit 1
    fi
  fi
}

cleanup_ports() {
  if command -v powershell.exe >/dev/null 2>&1; then
    powershell.exe -NoProfile -Command 'Get-NetTCPConnection -LocalPort 8080,5173 -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }' >/dev/null 2>&1 || true
  fi
}

kill_tree() {
  local pid="$1"
  if [ -z "$pid" ]; then
    return 0
  fi
  if command -v powershell.exe >/dev/null 2>&1; then
    powershell.exe -NoProfile -Command "Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue" >/dev/null 2>&1 || true
  elif command -v taskkill.exe >/dev/null 2>&1; then
    taskkill.exe //PID "$pid" //T //F >/dev/null 2>&1 || true
  elif command -v taskkill >/dev/null 2>&1; then
    taskkill //PID "$pid" //T //F >/dev/null 2>&1 || true
  else
    kill "$pid" >/dev/null 2>&1 || true
  fi
}

cleanup() {
  kill_tree "${BACKEND_PID:-}"
  kill_tree "${FRONTEND_PID:-}"
  cleanup_ports
}
trap cleanup EXIT

ensure_ports_free
bash "$ROOT_DIR/scripts/run_backend.sh" > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
bash "$ROOT_DIR/scripts/run_frontend.sh" > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!

wait_url() {
  local url="$1"
  local name="$2"
  for i in {1..90}; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "$name ready: $url"
      return 0
    fi
    sleep 2
  done
  echo "$name startup timeout: $url" >&2
  return 1
}

wait_url "http://127.0.0.1:8080/swagger-ui.html" "backend"
wait_url "http://127.0.0.1:5173/login" "frontend"
node "$ROOT_DIR/scripts/capture_screenshots.js"
cleanup
trap - EXIT
