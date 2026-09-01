package me.bookk.feature.employees.presentation.screen.list

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class EmployeeListDestinations : NavigationDestination() {
    data object Back : EmployeeListDestinations()
    data class AddEmployee(val businessId: Uuid) : EmployeeListDestinations()
}
