package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class FreeSlotsQuery(
    val businessId: Uuid,
    val employeeId: Uuid,
    val services: List<RequestedService>,
    val from: LocalDate,
    val to: LocalDate,
)

data class FreeSlotsDay(
    val date: LocalDate,
    val slots: List<FreeSlot>,
)

data class FreeSlot(
    val start: Instant,
    val end: Instant,
)
