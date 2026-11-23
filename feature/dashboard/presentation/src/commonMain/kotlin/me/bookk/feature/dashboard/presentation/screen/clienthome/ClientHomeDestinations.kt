package me.bookk.feature.dashboard.presentation.screen.clienthome

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientHomeDestinations : NavigationDestination() {
    data object Settings : ClientHomeDestinations()
    data object Bookings : ClientHomeDestinations()
    data object Businesses : ClientHomeDestinations()
    data class BookingDetails(
        val bookingId: Uuid,
    ) : ClientHomeDestinations()
    data class Services(
        val businessId: Uuid,
    ) : ClientHomeDestinations()
}
