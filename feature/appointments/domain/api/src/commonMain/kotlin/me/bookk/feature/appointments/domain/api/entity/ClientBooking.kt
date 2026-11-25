package me.bookk.feature.appointments.domain.api.entity

sealed interface ClientBooking {
    data class Visit(
        val appointment: Appointment,
    ) : ClientBooking

    data class Request(
        val request: AppointmentRequest,
    ) : ClientBooking
}
