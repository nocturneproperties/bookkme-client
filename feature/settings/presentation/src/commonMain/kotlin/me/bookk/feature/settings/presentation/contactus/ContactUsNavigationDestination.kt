package me.bookk.feature.settings.presentation.contactus

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class ContactUsNavigationDestination: NavigationDestination() {
    data object Back : ContactUsNavigationDestination()
}