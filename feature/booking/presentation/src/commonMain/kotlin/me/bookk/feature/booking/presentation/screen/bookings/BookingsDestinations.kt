package me.bookk.feature.booking.presentation.screen.bookings

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class BookingsDestinations : NavigationDestination() {
    data object Back : BookingsDestinations()

    data class RequestDetails(
        val requestId: Uuid,
    ) : BookingsDestinations()

    data class AppointmentDetails(
        val appointmentId: Uuid,
    ) : BookingsDestinations()
}
