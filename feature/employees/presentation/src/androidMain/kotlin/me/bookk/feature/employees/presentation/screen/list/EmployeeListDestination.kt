package me.bookk.feature.employees.presentation.screen.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.employees.presentation.EmployeesDestinations
import me.bookk.feature.employees.presentation.EmployeesNavigation
import me.bookk.feature.employees.presentation.LocalNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.employeeListScreen(navigation: EmployeesNavigation) {
    composable<EmployeesDestinations.EmployeeList> {
        val viewModel: EmployeeListViewModel = koinViewModel()
        CompositionLocalProvider(LocalNavigation provides navigation) {
            EmployeeListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    EmployeeListDestinations.Back -> navigation.onBack()
                    is EmployeeListDestinations.AddEmployee -> navigation.toInviteEmployee(it.businessId)
                }
            }
        }
    }
}
