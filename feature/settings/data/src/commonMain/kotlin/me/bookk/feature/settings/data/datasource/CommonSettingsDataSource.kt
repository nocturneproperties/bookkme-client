package me.bookk.feature.settings.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class CommonSettingsDataSource(
    preferenceProvider: PreferenceProvider,
) : DataSource(),
    SettingsDataSource {

    private val preferences = preferenceProvider.get("settings_prefs")

    override suspend fun setColorScheme(scheme: ColorScheme) {
        preferences.set(Key.colorScheme, scheme.id)
    }

    override suspend fun getColorScheme(): ColorScheme {
        return ColorScheme.from(preferences.get(Key.colorScheme))
    }

    override fun getColorSchemeFlow(): Flow<ColorScheme> {
        return preferences
            .getFlow(Key.colorScheme)
            .map { ColorScheme.from(it) }
    }

    private object Key {
        val colorScheme = Preferences.Key<Long>("color_scheme")
    }
}
