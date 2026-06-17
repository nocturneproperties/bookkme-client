package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class AppointmentDraft(
    val businessId: Uuid,
    val client: ClientSnapshot,
    val services: List<ServiceSnapshot>,
    val date: LocalDateTime,
    val note: String
)
