#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $0 <source-dir> <privacy|terms|dpa> <version> <effective-at>" >&2
    echo "Example: $0 ~/IdeaProjects/bookk-server/docs/legal/bookkme privacy 2026-12-01 2026-12-01T00:00:00Z" >&2
    exit 1
}

[[ $# -eq 4 ]] || usage
source_dir=$1
document=$2
version=$3
effective_at=$4
legal_dir=$(cd "$(dirname "$0")" && pwd)
target="$legal_dir/$document/$version"

case "$document" in
    privacy) source_name=privacy-policy; db_document=PRIVACY_POLICY ;;
    terms) source_name=terms-of-service; db_document=TERMS_OF_SERVICE ;;
    dpa) source_name=data-processing-agreement; db_document= ;;
    *) usage ;;
esac

[[ "$version" =~ ^[A-Za-z0-9._-]{1,32}$ ]] || { echo "Version must be 1-32 characters of A-Z, a-z, 0-9, '.', '_' or '-'." >&2; exit 1; }
[[ "$effective_at" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$ ]] || { echo "Effective time must be UTC, like 2026-12-01T00:00:00Z." >&2; exit 1; }
[[ ! -e "$target" ]] || { echo "$document/$version is already published and can't be changed. Publish a new version." >&2; exit 1; }

for language in en uk pl; do
    file="$source_dir/$source_name.$language.md"
    [[ -f "$file" ]] || { echo "Missing $file" >&2; exit 1; }
    ! grep -q '{{' "$file" || { echo "$file still contains a {{placeholder}}." >&2; exit 1; }
    head -5 "$file" | grep -qF "$version" || { echo "$file doesn't name version $version in its header." >&2; exit 1; }
done

mkdir -p "$target"
for language in en uk pl; do
    cp "$source_dir/$source_name.$language.md" "$target/$language.md"
done
echo "$effective_at" > "$target/effective-at.txt"

(cd "$legal_dir" && shasum -a 256 "$document/$version/"* >> published.sha256)

echo "Published $document/$version, effective $effective_at."
echo "Next: deploy the site and check https://bookkme.com/$document/$version is live."
if [[ -n "$db_document" ]]; then
    database_time=${effective_at/T/ }
    echo "Then insert the version in the user database (NocoDB 'user' source):"
    echo "INSERT INTO \`user\`.legal_document_version (document, version, url, effective_at)"
    echo "VALUES ('$db_document', '$version', 'https://bookkme.com/$document/$version', '${database_time%Z}');"
fi
