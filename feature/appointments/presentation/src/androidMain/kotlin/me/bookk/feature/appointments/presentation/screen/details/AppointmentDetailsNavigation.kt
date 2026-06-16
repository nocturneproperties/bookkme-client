package me.bookk.feature.appointments.presentation.screen.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsDestination.Back
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.appointmentDetailsScreen(navigation: AppointmentNavigation) {
    composable<AppointmentsDestination.Details>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: AppointmentsDestination.Details = it.toRoute()
        val viewModel: AppointmentDetailsViewModel = koinViewModel { parametersOf(route.appointmentId) }

        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        AppointmentDetailsScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { dest ->
            when (dest) {
                is Back -> navigation.onBack()
            }
        }
    }
}
