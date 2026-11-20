#!/usr/bin/env bash
# Usage: xcodebuild.sh <log-name> <xcodebuild arguments...>
# Prints only warnings and errors (xcbeautify, rendered as GitHub annotations) and keeps the full
# xcodebuild output in $RUNNER_TEMP/xcodebuild-logs/<log-name>.log for the failure artifact.
# xcbeautify drops lines it does not recognise, so on failure the Kotlin compiler errors and the
# Gradle failure summary from the embedAndSign script phase are printed from the full log.
set -euo pipefail

log_dir="$RUNNER_TEMP/xcodebuild-logs"
log_name=$1
log_file="$log_dir/$log_name.log"
shift
mkdir -p "$log_dir"

set +e
xcodebuild "$@" 2>&1 | tee "$log_file" | xcbeautify --renderer github-actions --quiet
status=$?
set -e

if [ "$status" -ne 0 ]; then
  sed -nE "s#^e: (file://)?${GITHUB_WORKSPACE}/([^:]+):([0-9]+):([0-9]+) (.*)#::error file=\2,line=\3,col=\4::\5#p" "$log_file"
  awk '/^FAILURE: /{show=1} /^\* Try:/{show=0} show' "$log_file"
  echo "Full xcodebuild output: xcodebuild-logs artifact ($log_name.log)"
fi
exit "$status"
