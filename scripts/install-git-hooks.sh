#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
HOOK_DIR="$ROOT_DIR/.git/hooks"

if [ ! -d "$HOOK_DIR" ]; then
  echo "ERROR: .git/hooks not found"
  exit 1
fi

for hook in commit-msg pre-push post-merge; do
  src="$ROOT_DIR/scripts/$hook"
  dst="$HOOK_DIR/$hook"
  if [ -f "$src" ]; then
    cp "$src" "$dst"
    chmod +x "$dst"
    echo "OK: installed $hook"
  else
    echo "WARN: missing $src"
  fi
done

echo "DONE: git hooks installed"