package me.bookk.feature.appointments.presentation.navigation

import kotlinx.serialization.Serializable

sealed class AppointmentsDestination {
    @Serializable
    data object RequestList : AppointmentsDestination()
}
