package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class AppointmentHistoryQuery(
    val businessId: Uuid,
    val search: String? = null,
)
