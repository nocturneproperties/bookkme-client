package me.bookk.feature.appointments.presentation.navigation

import androidx.compose.runtime.Composable
import kotlin.uuid.Uuid

class AppointmentNavigation(
    val createAppointment: (Uuid) -> Unit,
    val appointmentSettings: (Uuid) -> Unit,
    val details: (Uuid) -> Unit,
    val onBack: () -> Unit,
    val clientPicker: ClientPicker,
)

typealias ClientPicker = @Composable (onClientPicked: (Uuid) -> Unit, onDismiss: () -> Unit) -> Unit
