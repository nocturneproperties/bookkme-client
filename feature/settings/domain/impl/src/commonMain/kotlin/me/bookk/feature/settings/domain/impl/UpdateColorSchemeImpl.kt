package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class UpdateColorSchemeImpl(
    private val settingsDataSource: SettingsDataSource
) : UpdateColorScheme {
    override suspend fun invoke(scheme: ColorScheme) {
        settingsDataSource.setColorScheme(scheme)
    }
}