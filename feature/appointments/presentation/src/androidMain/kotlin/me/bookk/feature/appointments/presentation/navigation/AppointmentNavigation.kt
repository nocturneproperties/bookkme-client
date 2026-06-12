package me.bookk.feature.appointments.presentation.navigation

import kotlin.uuid.Uuid

class AppointmentNavigation(
    val createAppointment: (Uuid) -> Unit,
    val onBack: () -> Unit
)