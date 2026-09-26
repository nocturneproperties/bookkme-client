package me.bookk.feature.employees.presentation.screen.edit

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.employees.presentation.EmployeesDestinations
import me.bookk.feature.employees.presentation.EmployeesNavigation
import me.bookk.feature.employees.presentation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.editEmployeeScreen(navigation: EmployeesNavigation) {
    composable<EmployeesDestinations.EditEmployee>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: EmployeesDestinations.EditEmployee = it.toRoute()
        val viewModel: EditEmployeeViewModel = koinViewModel { parametersOf(route.id) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            EditEmployeeScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                EditEmployeeDestinations.Back -> navigation.onBack()
            }
        }
    }
}
