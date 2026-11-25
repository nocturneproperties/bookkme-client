package me.bookk.feature.booking.presentation.factory

import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.bookings.BookingsState
import me.bookk.feature.booking.presentation.screen.bookings.WebBookingsState
import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeState
import me.bookk.feature.booking.presentation.screen.datetime.WebBookingDateTimeState
import me.bookk.feature.booking.presentation.screen.details.BookingDetailsState
import me.bookk.feature.booking.presentation.screen.details.WebBookingDetailsState
import me.bookk.feature.booking.presentation.screen.review.BookingReviewState
import me.bookk.feature.booking.presentation.screen.review.WebBookingReviewState
import me.bookk.feature.booking.presentation.screen.sent.BookingSentState
import me.bookk.feature.booking.presentation.screen.sent.WebBookingSentState
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

    override fun createBookingDateTimeState(): BookingDateTimeState {
        return WebBookingDateTimeState()
    }

    override fun createBookingReviewState(): BookingReviewState {
        return WebBookingReviewState()
    }

    override fun createBookingSentState(): BookingSentState {
        return WebBookingSentState()
    }

    override fun createBookingDetailsState(): BookingDetailsState {
        return WebBookingDetailsState()
    }

    override fun createBookingsState(): BookingsState {
        return WebBookingsState()
    }
}
