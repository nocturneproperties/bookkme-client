package me.bookk.feature.business.presentation.screen.dashboard

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class DashboardNavigationDestination : NavigationDestination() {
    data object Employees : DashboardNavigationDestination()
    data class Clients(val id: Uuid) : DashboardNavigationDestination()
    data object Analytics : DashboardNavigationDestination()
    data class Settings(val id: Uuid) : DashboardNavigationDestination()
    data class Plugins(val id: Uuid) : DashboardNavigationDestination()
    data class Services(val id: Uuid) : DashboardNavigationDestination()
    data class History(val businessId: Uuid) : DashboardNavigationDestination()
    data object Requests : DashboardNavigationDestination()
    data class AppointmentSettings(val businessId: Uuid) : DashboardNavigationDestination()
    data object Assortment : DashboardNavigationDestination()
    data object Warehouse : DashboardNavigationDestination()
    data object Orders : DashboardNavigationDestination()
}