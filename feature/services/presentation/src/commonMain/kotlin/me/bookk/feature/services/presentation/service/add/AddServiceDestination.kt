package me.bookk.feature.services.presentation.service.add

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AddServiceDestination: NavigationDestination() {
    data object Back : AddServiceDestination()
}