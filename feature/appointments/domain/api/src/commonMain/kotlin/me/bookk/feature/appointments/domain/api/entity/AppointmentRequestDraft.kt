package me.bookk.feature.appointments.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class AppointmentRequestDraft(
    val businessId: Uuid,
    val employeeId: Uuid,
    val services: List<RequestedService>,
    val date: Instant,
    val note: String,
    val offerToken: String
)

data class RequestedService(
    val serviceId: Uuid,
    val count: Int
)
