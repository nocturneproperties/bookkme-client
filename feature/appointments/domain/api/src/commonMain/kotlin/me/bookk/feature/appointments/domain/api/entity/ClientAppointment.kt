package me.bookk.feature.appointments.domain.api.entity

data class ClientAppointment(
    val appointment: Appointment,
    val businessName: String,
)
