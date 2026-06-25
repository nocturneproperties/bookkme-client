package me.bookk.feature.appointments.presentation.screen.history

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryDestinations.AppointmentDetails
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryDestinations.Back
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.appointmentHistoryScreen(navigation: AppointmentNavigation) {
    composable<AppointmentsDestination.History>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: AppointmentsDestination.History = it.toRoute()
        val viewModel: AppointmentHistoryViewModel = koinViewModel { parametersOf(route.businessId) }

        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        AppointmentHistoryScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { dest ->
            when (dest) {
                is Back -> navigation.onBack()
                is AppointmentDetails -> navigation.details(dest.appointmentId)
            }
        }
    }
}
