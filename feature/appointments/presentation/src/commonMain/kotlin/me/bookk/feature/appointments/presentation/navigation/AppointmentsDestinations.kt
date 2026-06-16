package me.bookk.feature.appointments.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

sealed class AppointmentsDestination {
    @Serializable
    data object List : AppointmentsDestination()

    @Serializable
    data class Create(val businessId: Uuid) : AppointmentsDestination()
}
