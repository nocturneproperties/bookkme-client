package me.bookk.feature.appointments.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class AppointmentDraft(
    val businessId: Uuid,
    val client: ClientSnapshot,
    val services: List<ServiceSnapshot>,
    val date: Instant,
    val note: String
)
