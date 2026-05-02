package me.bookk.core

import kotlin.uuid.Uuid


fun String.makeUnique(): String {
    return "${this}_${Uuid.random()}"
}

fun String.discardUniqueness(): String {
    return substringBeforeLast("_")
}

fun String.capitalizeChar(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

fun String.monogram(): String {
    val words = trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "${words[0].first()}${words[1].first()}".uppercase()
    }
}

/**
 * Formats the string as IBAN with spaces every 4 characters.
 * Example: "GB12NNNN00123421000013" -> "GB12 NNNN 0012 3421 0000 13"
 */
fun String.formatAsIban(): String {
    return replace(" ", "").chunked(4).joinToString(" ")
}
