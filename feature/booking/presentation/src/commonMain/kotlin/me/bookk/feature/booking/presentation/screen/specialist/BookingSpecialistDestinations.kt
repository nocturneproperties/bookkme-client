package me.bookk.feature.booking.presentation.screen.specialist

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class BookingSpecialistDestinations : NavigationDestination() {
    data object Back : BookingSpecialistDestinations()
    data class DateTime(
        val businessId: Uuid,
    ) : BookingSpecialistDestinations()
}
