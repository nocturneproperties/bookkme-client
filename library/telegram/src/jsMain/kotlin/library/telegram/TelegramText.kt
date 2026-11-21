package library.telegram

private const val ELLIPSIS = "…"

internal fun String.limitTo(maxLength: Int): String {
    val text = trim()
    if (text.length <= maxLength) return text
    return text.take(maxLength - ELLIPSIS.length) + ELLIPSIS
}
