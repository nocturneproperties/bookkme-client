package me.bookk.feature.business.presentation.screen.settings.state

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BusinessSettingsDestination : NavigationDestination() {
    data object Back : BusinessSettingsDestination()
}