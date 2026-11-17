#!/usr/bin/env bash
set -euo pipefail

site_dir=$(cd "$(dirname "$0")" && pwd)
public="$site_dir/src/jsMain/resources/public"
colors="$site_dir/../designsystem/src/commonMain/moko-resources/colors/colors.xml"
favicon_svg="$public/favicon.svg"
app_icon_svg="$public/logo-dark.svg"
glyph_ratio=56
logo_width=247
logo_height=238

command -v rsvg-convert >/dev/null || { echo "rsvg-convert is required: brew install librsvg" >&2; exit 1; }

background=$(sed -n 's:.*<color name="darkBackground">\(#[0-9A-Fa-f]\{6\}\)</color>.*:\1:p' "$colors")
[[ -n "$background" ]] || { echo "darkBackground is not an opaque #RRGGBB color in $colors" >&2; exit 1; }

work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

render() {
    local svg=$1 size=$2 glyph_width=$3 output=$4 background_color=${5:-}
    local glyph_height=$((glyph_width * logo_height / logo_width))
    local options=(
        --width "$glyph_width" --height "$glyph_height"
        --page-width "$size" --page-height "$size"
        --left $(((size - glyph_width) / 2)) --top $(((size - glyph_height) / 2))
    )
    [[ -z "$background_color" ]] || options+=(--background-color "$background_color")
    rsvg-convert "${options[@]}" --output "$output" "$svg"
}

render_app_icon() {
    local size=$1 output=$2
    render "$app_icon_svg" "$size" $((size * glyph_ratio / 100)) "$output" "$background"
}

little_endian() {
    local value=$1 bytes=$2
    for ((i = 0; i < bytes; i++)); do
        printf "\\$(printf '%03o' $(((value >> (8 * i)) & 0xFF)))"
    done
}

write_ico() {
    local output=$1
    shift
    local offset=$((6 + 16 * $#))
    {
        little_endian 0 2
        little_endian 1 2
        little_endian $# 2
        for size in "$@"; do
            local length
            length=$(wc -c <"$work/favicon-$size.png" | tr -d ' ')
            little_endian "$size" 1
            little_endian "$size" 1
            little_endian 0 2
            little_endian 1 2
            little_endian 32 2
            little_endian "$length" 4
            little_endian "$offset" 4
            offset=$((offset + length))
        done
        for size in "$@"; do
            cat "$work/favicon-$size.png"
        done
    } >"$output"
}

favicon_sizes=(16 32 48)
for size in "${favicon_sizes[@]}"; do
    render "$favicon_svg" "$size" "$size" "$work/favicon-$size.png"
done
write_ico "$public/favicon.ico" "${favicon_sizes[@]}"

render_app_icon 180 "$public/apple-touch-icon.png"
render_app_icon 192 "$public/icon-192.png"
render_app_icon 512 "$public/icon-512.png"

echo "Generated favicon.ico, apple-touch-icon.png, icon-192.png and icon-512.png in $public"
