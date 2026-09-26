package me.bookk.feature.dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.dashboard.presentation.DashboardHomeNavigationDestination
import me.bookk.feature.dashboard.presentation.DashboardScreen
import me.bookk.feature.dashboard.presentation.DashboardViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

fun NavGraphBuilder.dashboardGraph(
    homeTab: @Composable () -> Unit,
    businessTab: @Composable () -> Unit,
    settingsTab: @Composable () -> Unit,
    onEnablePlugins: (Uuid) -> Unit
) {
    composable<DashboardDestination> {
        val viewModel: DashboardViewModel = koinViewModel()
        ObserveNotifications(viewModel.uiState.notifications)
        DashboardScreen(
            state = viewModel.uiState,
            homeScreen = homeTab,
            businessScreen = businessTab,
            settingsScreen = settingsTab
        )
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                is DashboardHomeNavigationDestination.EnablePlugins -> onEnablePlugins(it.businessId)
            }
        }
    }
}
