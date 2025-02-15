package me.bookk.feature.settings.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences
import me.bookk.feature.platform.domain.datasource.get
import me.bookk.feature.platform.domain.datasource.getFlow
import me.bookk.feature.platform.domain.datasource.set
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class CommonSettingsDataSource(
    preferenceProvider: PreferenceProvider
) : SettingsDataSource {

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