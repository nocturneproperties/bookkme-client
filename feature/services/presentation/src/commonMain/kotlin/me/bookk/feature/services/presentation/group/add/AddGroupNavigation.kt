package me.bookk.feature.services.presentation.group.add

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AddGroupNavigation : NavigationDestination() {
    data object Dismiss : AddGroupNavigation()
}
