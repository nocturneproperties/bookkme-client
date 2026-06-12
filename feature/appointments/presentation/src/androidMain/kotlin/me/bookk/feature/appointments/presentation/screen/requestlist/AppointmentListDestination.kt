package me.bookk.feature.appointments.presentation.screen.requestlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.appointmentRequestListScreen() {
    composable<AppointmentsDestination.RequestList> {
        val viewModel: AppointmentListViewModel = koinViewModel()

        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        AppointmentListScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { dest ->

        }
    }
}
