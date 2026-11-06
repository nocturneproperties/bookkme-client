package me.bookk.feature.settings.presentation.privacy

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class PrivacySettingsDestinations : NavigationDestination() {
    data object Back : PrivacySettingsDestinations()
}
