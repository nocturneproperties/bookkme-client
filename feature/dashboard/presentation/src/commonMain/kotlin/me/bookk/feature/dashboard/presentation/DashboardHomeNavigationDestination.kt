package me.bookk.feature.dashboard.presentation

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class DashboardHomeNavigationDestination : NavigationDestination() {
    data object CreateBusiness : DashboardHomeNavigationDestination()
    data class EnablePlugins(val businessId: Uuid) : DashboardHomeNavigationDestination()
}
