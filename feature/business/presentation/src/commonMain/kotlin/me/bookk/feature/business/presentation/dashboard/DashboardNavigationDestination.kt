package me.bookk.feature.business.presentation.dashboard

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class DashboardNavigationDestination : NavigationDestination() {
    data object Employees : DashboardNavigationDestination()
    data object Clients : DashboardNavigationDestination()
    data object Analytics : DashboardNavigationDestination()
    data object Settings : DashboardNavigationDestination()
    data object Services : DashboardNavigationDestination()
    data object History : DashboardNavigationDestination()
    data object AppointmentSettings : DashboardNavigationDestination()
    data object Assortment : DashboardNavigationDestination()
    data object Warehouse : DashboardNavigationDestination()
    data object Orders : DashboardNavigationDestination()
}