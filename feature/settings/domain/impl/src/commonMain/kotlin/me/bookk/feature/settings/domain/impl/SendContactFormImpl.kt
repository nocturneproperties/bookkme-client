package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.SendContactForm
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class SendContactFormImpl(
    private val settingsDataSource: SettingsDataSource
) : SendContactForm {
    override suspend fun invoke(text: String, includeLogs: Boolean) {
        //TODO add usage logs when feature will be implemented
        settingsDataSource.sendContactForm(text, null)
    }
}