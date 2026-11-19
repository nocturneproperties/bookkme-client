#!/bin/bash
set -u

cd "${CLAUDE_PROJECT_DIR:-$(pwd)}" || exit 0

input=$(cat)
stop_hook_active=$(printf '%s' "$input" | jq -r '.stop_hook_active // false' 2>/dev/null)

changed=$(git status --porcelain --untracked-files=all 2>/dev/null | awk '{print $NF}' | grep -E '\.kts?$' | sort)
[ -z "$changed" ] && exit 0

fingerprint=$( { printf '%s\n' "$changed"; printf '%s\n' "$changed" | while read -r f; do [ -f "$f" ] && shasum "$f"; done; } | shasum | cut -d' ' -f1)
marker="$(git rev-parse --git-dir)/claude-spotless-ok"
[ -f "$marker" ] && [ "$(cat "$marker")" = "$fingerprint" ] && exit 0

output=$(./gradlew spotlessCheck --continue -q 2>&1)
if [ $? -eq 0 ]; then
  printf '%s' "$fingerprint" > "$marker"
  exit 0
fi

if [ "$stop_hook_active" = "true" ]; then
  jq -n --arg msg "spotlessCheck still fails after a fix attempt; run ./gradlew spotlessCheck to see the violations." '{systemMessage: $msg}'
  exit 0
fi

echo "spotlessCheck failed. Run ./gradlew spotlessApply, fix whatever it cannot auto-fix, and re-run ./gradlew spotlessCheck until it passes:" >&2
printf '%s\n' "$output" | grep -v -E '^(\* (Try|Get more help)|> Run with|> Get more help|BUILD FAILED)' | head -80 >&2
exit 2
