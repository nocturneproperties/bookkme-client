package me.bookk.feature.settings.domain.api

interface SendContactForm {
    suspend operator fun invoke(text: String, includeLogs: Boolean)
}