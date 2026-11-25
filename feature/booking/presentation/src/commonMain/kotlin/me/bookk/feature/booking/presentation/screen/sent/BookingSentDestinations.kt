package me.bookk.feature.booking.presentation.screen.sent

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class BookingSentDestinations : NavigationDestination() {
    data object Home : BookingSentDestinations()
    data class Services(
        val businessId: Uuid,
    ) : BookingSentDestinations()

    data class RequestDetails(
        val requestId: Uuid,
    ) : BookingSentDestinations()

    data class AppointmentDetails(
        val appointmentId: Uuid,
    ) : BookingSentDestinations()
}
