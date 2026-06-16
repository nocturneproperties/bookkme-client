package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRequestIdRemote(val requestId: Uuid)
