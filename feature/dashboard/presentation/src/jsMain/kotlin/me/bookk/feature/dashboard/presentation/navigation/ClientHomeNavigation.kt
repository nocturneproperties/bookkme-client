package me.bookk.feature.dashboard.presentation.navigation

import me.bookk.core.domain.launch.ConfirmableAction
import kotlin.uuid.Uuid

class ClientHomeNavigation(
    val toSettings: () -> Unit,
    val toBookings: () -> Unit,
    val toRequestDetails: (Uuid) -> Unit,
    val toAppointmentDetails: (Uuid) -> Unit,
    val toServices: (Uuid) -> Unit,
    val toConfirmAction: (ConfirmableAction) -> Unit,
)
