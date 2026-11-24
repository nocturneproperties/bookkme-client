package me.bookk.feature.booking.presentation.navigation

import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import kotlin.uuid.Uuid

class BookingNavigation(
    val onBack: () -> Unit,
    val onLeave: () -> Unit,
    val toSpecialist: (Uuid) -> Unit,
    val toDateTime: (Uuid) -> Unit,
    val toReview: (Uuid) -> Unit,
    val toSent: (SentBookingRequest) -> Unit,
    val toNewBooking: (Uuid) -> Unit,
    val toBookingDetails: (Uuid) -> Unit,
)
