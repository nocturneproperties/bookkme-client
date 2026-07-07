package me.bookk.feature.settings.presentation.notifications

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class NotificationSettingsDestinations : NavigationDestination() {
    data object Back : NotificationSettingsDestinations()
}
