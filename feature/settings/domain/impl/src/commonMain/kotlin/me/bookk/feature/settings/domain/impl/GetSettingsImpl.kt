package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile

private const val TELEGRAM_PLACEHOLDER_EMAIL_DOMAIN = "telegramundeliverable.org"

internal class GetSettingsImpl(
    private val getColorScheme: GetColorScheme,
    private val userProfileCRUD: UserProfileCRUD,
) : GetSettings {
    override suspend fun invoke(): Settings {
        val profile = userProfileCRUD.get()
        return Settings(
            colorScheme = getColorScheme(),
            profile = SettingsProfile(
                firstName = profile.firstName,
                lastName = profile.lastName,
                email = profile.email,
                phone = profile.phone.orEmpty(),
                isEmailDeliverable = !isTelegramPlaceholder(profile.email),
            ),
        )
    }
}

private fun isTelegramPlaceholder(email: String): Boolean {
    val domain = email.trim().lowercase().substringAfterLast('@')
    return domain == TELEGRAM_PLACEHOLDER_EMAIL_DOMAIN ||
        domain.endsWith(".$TELEGRAM_PLACEHOLDER_EMAIL_DOMAIN")
}
