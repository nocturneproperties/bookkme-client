package me.bookk.feature.clients.domain.impl

import me.bookk.core.text.withoutDiacritics
import me.bookk.feature.clients.domain.api.entity.Client

internal class ClientSearch(
    query: String,
) {
    private val words = query.nameWords()
    private val text = query.trim().lowercase()
    private val digits = query.filter(Char::isDigit)
    private val isPhone =
        digits.isNotEmpty() && query.all { it.isDigit() || it in PHONE_SEPARATORS }

    fun matches(client: Client): Boolean {
        return matchesName(client) || matchesEmail(client) || matchesPhone(client)
    }

    private fun matchesName(client: Client): Boolean {
        val nameWords = "${client.name} ${client.lastName}".nameWords()
        return words.isNotEmpty() && words.all { word -> nameWords.any { it.startsWith(word) } }
    }

    private fun matchesEmail(client: Client): Boolean {
        return client.email?.lowercase()?.contains(text) == true
    }

    private fun matchesPhone(client: Client): Boolean {
        return isPhone && client.phone?.filter(Char::isDigit)?.contains(digits) == true
    }

    private fun String.nameWords(): List<String> {
        return withoutDiacritics()
            .replace(APOSTROPHES, "")
            .lowercase()
            .split(SEPARATORS)
            .filter { it.isNotEmpty() }
    }

    private companion object {
        const val PHONE_SEPARATORS = " +-()."
        val SEPARATORS = Regex("[\\s-]+")
        val APOSTROPHES = Regex("['’ʼ`]")
    }
}
