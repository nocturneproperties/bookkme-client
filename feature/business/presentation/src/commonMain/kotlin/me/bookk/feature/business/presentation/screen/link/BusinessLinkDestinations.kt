package me.bookk.feature.business.presentation.screen.link

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BusinessLinkDestinations : NavigationDestination() {
    data object Back : BusinessLinkDestinations()
}
