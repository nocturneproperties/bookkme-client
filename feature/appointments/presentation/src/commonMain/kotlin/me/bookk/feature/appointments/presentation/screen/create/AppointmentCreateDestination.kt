package me.bookk.feature.appointments.presentation.screen.create

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AppointmentCreateDestination : NavigationDestination() {
    data object Back : AppointmentCreateDestination()
}
