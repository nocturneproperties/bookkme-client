package me.bookk.feature.booking.presentation.factory

import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.services.BookingServicesState
import me.bookk.feature.booking.presentation.screen.services.WebBookingServicesState
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistState
import me.bookk.feature.booking.presentation.screen.specialist.WebBookingSpecialistState

internal class WebBookingStateFactory : BookingStateFactory {
    override fun createBookingServicesState(): BookingServicesState {
        return WebBookingServicesState()
    }

    override fun createBookingSpecialistState(): BookingSpecialistState {
        return WebBookingSpecialistState()
    }
}
