#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR/backend"

if command -v mvn >/dev/null 2>&1; then
  MVN_CMD="mvn"
elif [ -x "$ROOT_DIR/.tools/apache-maven-3.9.9/bin/mvn" ]; then
  MVN_CMD="$ROOT_DIR/.tools/apache-maven-3.9.9/bin/mvn"
elif [ -f "$ROOT_DIR/.tools/apache-maven-3.9.9/bin/mvn.cmd" ]; then
  MVN_CMD="$ROOT_DIR/.tools/apache-maven-3.9.9/bin/mvn.cmd"
else
  echo "Maven is not available. Install Maven 3.9+ or keep project/.tools/apache-maven-3.9.9." >&2
  exit 1
fi

if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
  JAVA_CMD="java"
else
  echo "Java is not available. Install JDK 17 and set JAVA_HOME." >&2
  exit 1
fi

"$MVN_CMD" -DskipTests package
exec "$JAVA_CMD" -jar "$ROOT_DIR/backend/target/cqupt-visitor-backend-0.1.0.jar"
