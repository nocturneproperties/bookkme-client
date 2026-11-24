package me.bookk.feature.booking.presentation.navigation

import kotlin.uuid.Uuid

class BookingNavigation(
    val onBack: () -> Unit,
    val toSpecialist: (Uuid) -> Unit,
    val toDateTime: (Uuid) -> Unit,
    val toReview: (Uuid) -> Unit,
)
