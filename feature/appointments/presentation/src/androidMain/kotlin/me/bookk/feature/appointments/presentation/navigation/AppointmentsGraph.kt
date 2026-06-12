package me.bookk.feature.appointments.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.appointments.presentation.screen.requestlist.appointmentRequestListScreen

fun NavGraphBuilder.appointmentsGraph(navigation: AppointmentNavigation) {
    appointmentRequestListScreen(navigation)
}
