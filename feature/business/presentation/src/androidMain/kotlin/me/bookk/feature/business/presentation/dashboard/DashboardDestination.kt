package me.bookk.feature.business.presentation.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.dashboardScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Dashboard>(
        enterTransition = {
            scaleIn(
                initialScale = 0.98f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {
        val viewModel: BusinessDashboardViewModel = koinViewModel()
        val listener = DashboardEventListener(
            onItemClicked = {
                when (val navItem = it.navigation) {
                    DashboardNavigationDestination.Analytics -> navigation.toAnalytics()
                    DashboardNavigationDestination.AppointmentSettings -> navigation.toAppointmentSettings()
                    DashboardNavigationDestination.Assortment -> navigation.toShopAssortment()
                    is DashboardNavigationDestination.Clients -> navigation.toClients(navItem.id)
                    DashboardNavigationDestination.Employees -> navigation.toEmployees()
                    DashboardNavigationDestination.History -> navigation.toAppointmentHistory()
                    DashboardNavigationDestination.Orders -> navigation.toShopOrders()
                    DashboardNavigationDestination.Services -> navigation.toAppointmentServices()
                    is DashboardNavigationDestination.Settings -> navigation.toBusinessSettings(navItem.id)
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