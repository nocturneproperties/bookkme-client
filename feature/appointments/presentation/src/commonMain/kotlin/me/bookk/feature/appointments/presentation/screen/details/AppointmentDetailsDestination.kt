package me.bookk.feature.appointments.presentation.screen.details

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AppointmentDetailsDestination : NavigationDestination() {
    data object Back : AppointmentDetailsDestination()
}
