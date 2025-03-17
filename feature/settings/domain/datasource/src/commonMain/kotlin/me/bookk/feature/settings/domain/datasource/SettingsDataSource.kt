package me.bookk.feature.settings.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.ColorScheme

interface SettingsDataSource {
    suspend fun setColorScheme(scheme: ColorScheme)
    suspend fun getColorScheme(): ColorScheme
    fun getColorSchemeFlow(): Flow<ColorScheme>

    suspend fun sendContactForm(text: String, logs: String?)
}