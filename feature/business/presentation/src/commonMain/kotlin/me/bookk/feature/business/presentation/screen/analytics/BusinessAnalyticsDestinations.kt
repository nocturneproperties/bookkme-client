package me.bookk.feature.business.presentation.screen.analytics

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BusinessAnalyticsDestinations : NavigationDestination() {
    data object Back : BusinessAnalyticsDestinations()
}
