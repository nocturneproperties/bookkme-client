package me.bookk.feature.booking.presentation.factory

import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.services.BookingServicesState
import me.bookk.feature.booking.presentation.screen.services.WebBookingServicesState

internal class WebBookingStateFactory : BookingStateFactory {
    override fun createBookingServicesState(): BookingServicesState {
        return WebBookingServicesState()
    }
}
