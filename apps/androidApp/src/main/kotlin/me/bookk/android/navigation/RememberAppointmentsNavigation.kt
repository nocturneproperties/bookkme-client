package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import me.bookk.feature.appointments.presentation.navigation.ClientPicker
import me.bookk.feature.clients.presentation.select.SelectClientSheet
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberAppointmentsNavigation(controller: NavController) = remember(controller) {
    AppointmentNavigation(
        createAppointment = { controller.navigate(AppointmentsDestination.Create(it)) },
        details = { controller.navigate(AppointmentsDestination.Details(it)) },
        appointmentSettings = { controller.navigate(AppointmentsDestination.Settings(it)) },
        onBack = { controller.popBackStack() },
        clientPicker = selectClientPicker,
    )
}

val selectClientPicker: ClientPicker = { onClientPicked, onDismiss ->
    SelectClientSheet(onClientSelected = onClientPicked, onDismiss = onDismiss)
}
