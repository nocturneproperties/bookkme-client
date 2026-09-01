package me.bookk.feature.business.presentation.screen.dashboard

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
                    is DashboardNavigationDestination.AppointmentSettings -> navigation.toAppointmentSettings(navItem.businessId)
                    DashboardNavigationDestination.Assortment -> navigation.toShopAssortment()
                    is DashboardNavigationDestination.Clients -> navigation.toClients(navItem.id)
                    is DashboardNavigationDestination.Requests -> navigation.toAppointmentRequests()
                    is DashboardNavigationDestination.Employees -> navigation.toEmployees(navItem.id)
                    is DashboardNavigationDestination.History -> navigation.toAppointmentHistory(navItem.businessId)
                    DashboardNavigationDestination.Orders -> navigation.toShopOrders()
                    is DashboardNavigationDestination.Services -> navigation.toBusinessServices(navItem.id)
                    is DashboardNavigationDestination.Settings -> navigation.toBusinessSettings(navItem.id)
                    is DashboardNavigationDestination.Plugins -> navigation.toBusinessPlugins(navItem.id)
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