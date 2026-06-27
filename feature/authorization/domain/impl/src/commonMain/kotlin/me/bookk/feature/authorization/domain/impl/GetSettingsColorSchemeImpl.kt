package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.authorization.domain.api.GetSettingsColorScheme
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.authorization.domain.entity.ColorScheme as AuthColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme as SettingsColorScheme

internal class GetSettingsColorSchemeImpl(
    private val getColorScheme: GetColorScheme
) : GetSettingsColorScheme {

    override suspend fun invoke(): AuthColorScheme = getColorScheme().toAuthColorScheme()

    override fun asFlow(): Flow<AuthColorScheme> = getColorScheme.asFlow().map { it.toAuthColorScheme() }

    private fun SettingsColorScheme.toAuthColorScheme(): AuthColorScheme = when (this) {
        SettingsColorScheme.DARK -> AuthColorScheme.DARK
        SettingsColorScheme.LIGHT -> AuthColorScheme.LIGHT
        SettingsColorScheme.SYSTEM -> AuthColorScheme.SYSTEM
    }
}
