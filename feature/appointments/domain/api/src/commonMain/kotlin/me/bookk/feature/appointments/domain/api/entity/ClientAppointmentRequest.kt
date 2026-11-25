package me.bookk.feature.appointments.domain.api.entity

data class ClientAppointmentRequest(
    val request: AppointmentRequest,
    val businessName: String,
)
