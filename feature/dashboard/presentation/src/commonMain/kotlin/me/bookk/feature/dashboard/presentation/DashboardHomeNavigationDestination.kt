package me.bookk.feature.dashboard.presentation

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class DashboardHomeNavigationDestination : NavigationDestination() {
    data class EnablePlugins(val businessId: Uuid) : DashboardHomeNavigationDestination()
    data object AddServiceGroup : DashboardHomeNavigationDestination()
    data class AddService(val businessId: Uuid) : DashboardHomeNavigationDestination()
    data class AddClient(val businessId: Uuid) : DashboardHomeNavigationDestination()
}
