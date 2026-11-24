package me.bookk.feature.booking.presentation.screen.datetime

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class BookingDateTimeDestinations : NavigationDestination() {
    data object Back : BookingDateTimeDestinations()
    data object Leave : BookingDateTimeDestinations()
    data class Review(
        val businessId: Uuid,
    ) : BookingDateTimeDestinations()
}
