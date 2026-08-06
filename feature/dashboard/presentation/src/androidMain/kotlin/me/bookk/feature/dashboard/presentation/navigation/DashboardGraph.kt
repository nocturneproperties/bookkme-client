package me.bookk.feature.dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    createBusinessSheet: @Composable (onDismiss: () -> Unit) -> Unit,
    onEnablePlugins: (Uuid) -> Unit
) {
    composable<DashboardDestination> {
        val viewModel: DashboardViewModel = koinViewModel()
        var isCreateBusinessSheetVisible by remember { mutableStateOf(false) }

        ObserveNotifications(viewModel.uiState.notifications)
        DashboardScreen(
            state = viewModel.uiState,
            homeScreen = homeTab,
            businessScreen = businessTab,
            settingsScreen = settingsTab
        )
        if (isCreateBusinessSheetVisible) {
            createBusinessSheet { isCreateBusinessSheetVisible = false }
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                DashboardHomeNavigationDestination.CreateBusiness -> isCreateBusinessSheetVisible = true
                is DashboardHomeNavigationDestination.EnablePlugins -> onEnablePlugins(it.businessId)
            }
        }
    }
}
