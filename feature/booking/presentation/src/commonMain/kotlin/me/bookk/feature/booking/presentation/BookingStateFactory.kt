package me.bookk.feature.booking.presentation

import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeState
import me.bookk.feature.booking.presentation.screen.services.BookingServicesState
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistState

interface BookingStateFactory {
    fun createBookingServicesState(): BookingServicesState
    fun createBookingSpecialistState(): BookingSpecialistState
    fun createBookingDateTimeState(): BookingDateTimeState
}
