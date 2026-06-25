package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberAppointmentsNavigation(controller: NavController) = remember(controller) {
    AppointmentNavigation(
        createAppointment = { controller.navigate(AppointmentsDestination.Create(it)) },
        requests = { controller.navigate(AppointmentsDestination.Request(it)) },
        details = { controller.navigate(AppointmentsDestination.Details(it)) },
        appointmentSettings = { controller.navigate(AppointmentsDestination.Settings(it)) },
        onBack = { controller.popBackStack() },
    )
}