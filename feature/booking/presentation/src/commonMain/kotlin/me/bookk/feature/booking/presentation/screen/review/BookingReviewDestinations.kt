package me.bookk.feature.booking.presentation.screen.review

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest

sealed class BookingReviewDestinations : NavigationDestination() {
    data object Back : BookingReviewDestinations()
    data object Leave : BookingReviewDestinations()
    data class Sent(
        val request: SentBookingRequest,
    ) : BookingReviewDestinations()
}
