package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class GetColorSchemeImpl(
    private val settingsDataSource: SettingsDataSource
) : GetColorScheme {
    override suspend fun invoke(): ColorScheme {
        return settingsDataSource.getColorScheme()
    }

    override fun asFlow(): Flow<ColorScheme> {
        return settingsDataSource.getColorSchemeFlow()
    }
}