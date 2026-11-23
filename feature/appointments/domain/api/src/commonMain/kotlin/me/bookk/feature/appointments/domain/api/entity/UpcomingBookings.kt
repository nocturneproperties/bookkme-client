package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class UpcomingBookings(
    val appointments: List<Appointment>,
    val requests: List<AppointmentRequest>,
) {
    val businessIds: Set<Uuid>
        get() = (appointments.map { it.businessId } + requests.map { it.businessId }).toSet()
}
