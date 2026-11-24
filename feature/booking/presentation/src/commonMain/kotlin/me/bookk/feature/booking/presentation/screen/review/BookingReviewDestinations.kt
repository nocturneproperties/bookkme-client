package me.bookk.feature.booking.presentation.screen.review

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class BookingReviewDestinations : NavigationDestination() {
    data object Back : BookingReviewDestinations()
    data object Leave : BookingReviewDestinations()
    data class Sent(
        val businessId: Uuid,
    ) : BookingReviewDestinations()
}
