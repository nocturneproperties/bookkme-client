package me.bookk.feature.settings.presentation.navigation

import androidx.compose.runtime.compositionLocalOf

class SettingsNavigation(
    val navigateBack: () -> Unit,
    val navigateToEditProfile: () -> Unit,
    val navigateToChangeEmail: () -> Unit,
    val navigateToPasskey: () -> Unit,
    val navigateToNotifications: () -> Unit,
    val navigateToPersonalDataExport: () -> Unit,
    val navigateToPrivacy: () -> Unit,
    val navigateToDeleteAccount: () -> Unit,
)

internal val LocalNavigation = compositionLocalOf {
    SettingsNavigation(
        navigateBack = {},
        navigateToEditProfile = {},
        navigateToChangeEmail = {},
        navigateToPasskey = {},
        navigateToDeleteAccount = {},
        navigateToNotifications = {},
        navigateToPersonalDataExport = {},
        navigateToPrivacy = {},
    )
}
