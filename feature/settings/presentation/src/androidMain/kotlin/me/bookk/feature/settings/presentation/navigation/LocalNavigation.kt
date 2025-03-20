package me.bookk.feature.settings.presentation.navigation

import androidx.compose.runtime.compositionLocalOf

class SettingsNavigation(
    val navigateBack: () -> Unit,
    val navigateToEditProfile: () -> Unit,
    val navigateToPasskey: () -> Unit,
    val navigateToDeleteAccount: () -> Unit,
    val navigateToContact: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    SettingsNavigation(
        navigateBack = {},
        navigateToEditProfile = {},
        navigateToPasskey = {},
        navigateToDeleteAccount = {},
        navigateToContact = {}
    )
}