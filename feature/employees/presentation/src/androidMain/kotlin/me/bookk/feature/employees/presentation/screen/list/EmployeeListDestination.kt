package me.bookk.feature.employees.presentation.screen.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.employees.presentation.EmployeesDestinations
import me.bookk.feature.employees.presentation.EmployeesNavigation
import me.bookk.feature.employees.presentation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.employeeListScreen(navigation: EmployeesNavigation) {
    composable<EmployeesDestinations.EmployeeList>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: EmployeesDestinations.EmployeeList = it.toRoute()
        val viewModel: EmployeeListViewModel = koinViewModel { parametersOf(route.id) }
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
