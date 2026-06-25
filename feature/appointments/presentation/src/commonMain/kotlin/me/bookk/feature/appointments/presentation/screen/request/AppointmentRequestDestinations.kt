package me.bookk.feature.appointments.presentation.screen.request

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AppointmentRequestDestinations : NavigationDestination() {
    data object Back : AppointmentRequestDestinations()
}
