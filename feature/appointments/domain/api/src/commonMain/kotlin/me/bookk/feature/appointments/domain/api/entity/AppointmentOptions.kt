package me.bookk.feature.appointments.domain.api.entity

class AppointmentOptions(
    val settings: AppointmentSettings,
    val clients: List<ClientSnapshot>,
    val services: List<ServiceSnapshot>
)