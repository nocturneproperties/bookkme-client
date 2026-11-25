package me.bookk.feature.booking.presentation.screen.details

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BookingDetailsDestinations : NavigationDestination() {
    data object Back : BookingDetailsDestinations()
    data object Bookings : BookingDetailsDestinations()
}
