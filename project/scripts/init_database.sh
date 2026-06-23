#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USERNAME="${DB_USERNAME:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"
MYSQL_ARGS=(-h "$DB_HOST" -P "$DB_PORT" -u "$DB_USERNAME")

if [ -n "$DB_PASSWORD" ]; then
  MYSQL_ARGS+=(-p"$DB_PASSWORD")
fi

if ! command -v mysql >/dev/null 2>&1; then
  echo "mysql client is not available. Add MySQL Server bin directory to PATH." >&2
  exit 1
fi

mysql "${MYSQL_ARGS[@]}" < "$ROOT_DIR/database/create_database.sql"
mysql "${MYSQL_ARGS[@]}" < "$ROOT_DIR/database/schema.sql"
mysql "${MYSQL_ARGS[@]}" < "$ROOT_DIR/database/seed.sql"
echo "Database initialized: cqupt_visitor_system"
