package me.bookk.feature.settings.presentation.dashboard

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class SettingsDashboardDestination : NavigationDestination() {
    data object EditProfile : SettingsDashboardDestination()
}