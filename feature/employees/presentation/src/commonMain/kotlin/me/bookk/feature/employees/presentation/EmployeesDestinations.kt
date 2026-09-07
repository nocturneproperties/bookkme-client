package me.bookk.feature.employees.presentation

import kotlinx.serialization.Serializable
import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class EmployeesDestinations : NavigationDestination() {
    @Serializable
    data object EmployeeList : EmployeesDestinations()
    @Serializable
    data class InviteEmployee(val businessId: Uuid) : EmployeesDestinations()
}
