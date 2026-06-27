package me.bookk.feature.appointments.presentation.screen.list

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class AppointmentListDestinations : NavigationDestination() {
    data class CreateAppointment(val businessId: Uuid) : AppointmentListDestinations()
    data class AppointmentDetails(val appointmentId: Uuid) : AppointmentListDestinations()
}
