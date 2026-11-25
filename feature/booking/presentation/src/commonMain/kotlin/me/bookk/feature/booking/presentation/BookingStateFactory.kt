package me.bookk.feature.booking.presentation

import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeState
import me.bookk.feature.booking.presentation.screen.details.BookingDetailsState
import me.bookk.feature.booking.presentation.screen.review.BookingReviewState
import me.bookk.feature.booking.presentation.screen.sent.BookingSentState
import me.bookk.feature.booking.presentation.screen.services.BookingServicesState
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistState

interface BookingStateFactory {
    fun createBookingServicesState(): BookingServicesState
    fun createBookingSpecialistState(): BookingSpecialistState
    fun createBookingDateTimeState(): BookingDateTimeState
    fun createBookingReviewState(): BookingReviewState
    fun createBookingSentState(): BookingSentState
    fun createBookingDetailsState(): BookingDetailsState
}
