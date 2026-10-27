package me.bookk.feature.business.presentation.screen.plugins

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BusinessPluginsDestinations : NavigationDestination() {
    data object Back : BusinessPluginsDestinations()
}