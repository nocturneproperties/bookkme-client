package me.bookk.feature.employees.presentation.screen.invite

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

internal fun NavGraphBuilder.inviteEmployeeScreen(navigation: EmployeesNavigation) {
    composable<EmployeesDestinations.InviteEmployee>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: EmployeesDestinations.InviteEmployee = it.toRoute()
        val viewModel: InviteEmployeeViewModel = koinViewModel { parametersOf(route.businessId) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            InviteEmployeeScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    InviteEmployeeDestinations.Back -> navigation.onBack()
                }
            }
        }
    }
}
