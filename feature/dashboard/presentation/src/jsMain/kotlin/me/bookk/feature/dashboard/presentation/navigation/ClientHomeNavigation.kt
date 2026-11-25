package me.bookk.feature.dashboard.presentation.navigation

import kotlin.uuid.Uuid

class ClientHomeNavigation(
    val toSettings: () -> Unit,
    val toBookings: () -> Unit,
    val toBusinesses: () -> Unit,
    val toRequestDetails: (Uuid) -> Unit,
    val toAppointmentDetails: (Uuid) -> Unit,
    val toServices: (Uuid) -> Unit,
)
