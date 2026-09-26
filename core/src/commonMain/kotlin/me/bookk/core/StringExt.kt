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

fun String?.dashOnBlank(): String {
    return this?.ifBlank { "-" } ?: "-"
}
