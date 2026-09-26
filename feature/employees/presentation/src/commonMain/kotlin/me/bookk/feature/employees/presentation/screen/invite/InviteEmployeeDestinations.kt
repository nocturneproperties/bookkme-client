package me.bookk.feature.employees.presentation.screen.invite

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class InviteEmployeeDestinations : NavigationDestination() {
    data object Back : InviteEmployeeDestinations()
}
