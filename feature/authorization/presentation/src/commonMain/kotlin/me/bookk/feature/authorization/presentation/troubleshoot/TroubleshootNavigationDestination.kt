package me.bookk.feature.authorization.presentation.troubleshoot

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class TroubleshootNavigationDestination : NavigationDestination() {
    data object Back : TroubleshootNavigationDestination()
    data object ContactSupport : TroubleshootNavigationDestination()
}