package me.bookk.feature.appointments.presentation.screen.request

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestDestinations.Back
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.appointmentRequestScreen(navigation: AppointmentNavigation) {
    composable<AppointmentsDestination.Request>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: AppointmentsDestination.Request = it.toRoute()
        val viewModel: AppointmentRequestViewModel = koinViewModel { parametersOf(route.businessId) }

        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        AppointmentRequestScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { dest ->
            when (dest) {
                is Back -> navigation.onBack()
            }
        }
    }
}
