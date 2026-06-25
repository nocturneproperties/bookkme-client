package me.bookk.feature.appointments.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

sealed class AppointmentsDestination {
    @Serializable
    data object List : AppointmentsDestination()

    @Serializable
    data class History(val businessId: Uuid) : AppointmentsDestination()

    @Serializable
    data class Create(val businessId: Uuid) : AppointmentsDestination()

    @Serializable
    data class Details(val appointmentId: Uuid) : AppointmentsDestination()

    @Serializable
    data class Settings(val businessId: Uuid) : AppointmentsDestination()

    @Serializable
    data class Request(val businessId: Uuid) : AppointmentsDestination()
}
