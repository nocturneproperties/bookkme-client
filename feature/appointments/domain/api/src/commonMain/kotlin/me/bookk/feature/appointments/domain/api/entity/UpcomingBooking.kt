package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class UpcomingBooking(
    val id: Uuid,
    val businessId: Uuid,
    val businessName: String,
    val services: List<ServiceSnapshot>,
    val employee: EmployeeSnapshot,
    val date: LocalDateTime,
    val status: Status,
) {
    enum class Status {
        CONFIRMED,
        WAITING_FOR_CONFIRMATION,
    }
}
