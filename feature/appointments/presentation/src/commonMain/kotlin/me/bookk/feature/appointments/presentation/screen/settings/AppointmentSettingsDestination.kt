package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class AppointmentSettingsDestination : NavigationDestination() {
    data object Back : AppointmentSettingsDestination()
}
