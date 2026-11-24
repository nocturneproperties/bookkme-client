package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class BookingInfo(
    val businessId: Uuid,
    val appointmentNote: String,
)
