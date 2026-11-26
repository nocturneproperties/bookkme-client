package me.bookk.core.text

private val combiningMarks = setOf(
    CharCategory.NON_SPACING_MARK,
    CharCategory.COMBINING_SPACING_MARK,
    CharCategory.ENCLOSING_MARK,
)

fun String.withoutDiacritics(): String {
    return canonicallyDecomposed().filterNot { it.category in combiningMarks }
}

internal expect fun String.canonicallyDecomposed(): String
