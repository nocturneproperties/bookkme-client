package me.bookk.feature.dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.dashboard.presentation.DashboardScreen
import me.bookk.feature.dashboard.presentation.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.dashboardGraph(
    appointmentsScreen: @Composable () -> Unit,
    businessScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit
) {
    composable<DashboardDestination> {
        val viewModel: DashboardViewModel = koinViewModel()

        DashboardScreen(
            state = viewModel.uiState,
            appointmentsScreen = appointmentsScreen,
            businessScreen = businessScreen,
            settingsScreen = settingsScreen
        )
    }
}