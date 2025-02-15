package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile

internal class GetSettingsImpl(
    private val getColorScheme: GetColorScheme,
    private val userProfileCRUD: UserProfileCRUD
) : GetSettings {
    override suspend fun invoke(): Settings {
        val profile = runCatching {
            userProfileCRUD.get()
        }.getOrElse {
            UserProfile(0L, "John", "Doe", "email@example.com")
        }
        return Settings(
            colorScheme = getColorScheme(),
            profile = SettingsProfile(
                firstName = profile.firstName,
                lastName = profile.lastName,
                email = profile.email
            )
        )
    }
}