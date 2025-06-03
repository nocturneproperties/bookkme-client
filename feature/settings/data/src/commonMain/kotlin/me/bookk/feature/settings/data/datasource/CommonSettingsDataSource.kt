package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.feature.settings.data.remote.api.UserRouting
import me.bookk.feature.settings.data.remote.model.ContactFormRemote
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource

internal class CommonSettingsDataSource(
    private val httpClient: HttpClient,
    preferenceProvider: PreferenceProvider
) : DataSource(), SettingsDataSource {

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

    override suspend fun sendContactForm(text: String, logs: String?) {
        mapExceptions {
            httpClient.post(UserRouting.Api.User.ContactUs()) {
                setBody(ContactFormRemote(text, logs))
            }
        }
    }

    private object Key {
        val colorScheme = Preferences.Key<Long>("color_scheme")
    }
}