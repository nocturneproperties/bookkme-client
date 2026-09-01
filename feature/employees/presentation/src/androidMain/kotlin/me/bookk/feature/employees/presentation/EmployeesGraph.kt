package me.bookk.feature.employees.presentation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.employees.presentation.screen.invite.inviteEmployeeScreen
import me.bookk.feature.employees.presentation.screen.list.employeeListScreen

fun NavGraphBuilder.employeesGraph(navigation: EmployeesNavigation) {
    employeeListScreen(navigation)
    inviteEmployeeScreen(navigation)
}
