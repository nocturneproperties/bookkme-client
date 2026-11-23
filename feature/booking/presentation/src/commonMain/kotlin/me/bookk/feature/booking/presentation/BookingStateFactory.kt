package me.bookk.feature.booking.presentation

import me.bookk.feature.booking.presentation.screen.services.BookingServicesState

interface BookingStateFactory {
    fun createBookingServicesState(): BookingServicesState
}
