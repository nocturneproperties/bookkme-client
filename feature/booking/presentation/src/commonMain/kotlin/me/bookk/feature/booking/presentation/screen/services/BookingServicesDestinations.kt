package me.bookk.feature.booking.presentation.screen.services

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import kotlin.uuid.Uuid

sealed class BookingServicesDestinations : NavigationDestination() {
    data object Back : BookingServicesDestinations()
    data class Specialist(
        val businessId: Uuid,
        val services: List<RequestedService>,
    ) : BookingServicesDestinations()
}
