package me.bookk.feature.appointments.presentation.screen.history

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class AppointmentHistoryDestinations : NavigationDestination() {
    data object Back : AppointmentHistoryDestinations()
    data class AppointmentDetails(val appointmentId: Uuid) : AppointmentHistoryDestinations()
}
