package me.bookk.feature.employees.presentation.screen.edit

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class EditEmployeeDestinations : NavigationDestination() {
    data object Back : EditEmployeeDestinations()
}
