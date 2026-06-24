package me.bookk.feature.appointments.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.appointments.presentation.screen.create.appointmentCreateScreen
import me.bookk.feature.appointments.presentation.screen.details.appointmentDetailsScreen
import me.bookk.feature.appointments.presentation.screen.requestlist.appointmentRequestListScreen
import me.bookk.feature.appointments.presentation.screen.settings.appointmentSettingsScreen

fun NavGraphBuilder.appointmentsGraph(navigation: AppointmentNavigation) {
    appointmentRequestListScreen(navigation)
    appointmentCreateScreen(navigation)
    appointmentDetailsScreen(navigation)
    appointmentSettingsScreen(navigation)
}
