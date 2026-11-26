package me.bookk.feature.appointments.domain.api.entity

data class UpcomingBookings(
    val appointments: List<Appointment>,
    val requests: List<AppointmentRequest>,
)
