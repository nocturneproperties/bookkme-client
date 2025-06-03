package me.bookk.feature.business.presentation.dashboard

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.dashboardScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Dashboard> {
        val viewModel: BusinessDashboardViewModel = koinViewModel()
        val listener = DashboardEventListener(
            onItemClicked = {
                when (it.navigation) {
                    DashboardNavigationDestination.Analytics -> navigation.toAnalytics()
                    DashboardNavigationDestination.AppointmentSettings -> navigation.toAppointmentSettings()
                    DashboardNavigationDestination.Assortment -> navigation.toShopAssortment()
                    DashboardNavigationDestination.Clients -> navigation.toClients()
                    DashboardNavigationDestination.Employees -> navigation.toEmployees()
                    DashboardNavigationDestination.History -> navigation.toAppointmentHistory()
                    DashboardNavigationDestination.Orders -> navigation.toShopOrders()
                    DashboardNavigationDestination.Services -> navigation.toAppointmentServices()
                    DashboardNavigationDestination.Settings -> navigation.toBusinessSettings()
                    DashboardNavigationDestination.Warehouse -> navigation.toShopWarehouse()
                }
            }
        )
        CompositionLocalProvider(
            LocalDashboardEventListener provides listener
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            DashboardScreen(viewModel.uiState)
        }
    }
}