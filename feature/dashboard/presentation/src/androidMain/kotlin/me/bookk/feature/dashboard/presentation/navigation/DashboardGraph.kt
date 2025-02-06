package me.bookk.feature.dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.dashboard.presentation.DashboardScreen
import me.bookk.feature.dashboard.presentation.DashboardViewModel
import me.bookk.feature.dashboard.presentation.state.TabItem
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.dashboardGraph(
    appointmentsScreen: @Composable () -> Unit,
    businessScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit
) {
    composable(route = DashboardDestination.route) {
        val viewModel: DashboardViewModel = koinViewModel()

        HandleNavigation(
            viewModel = viewModel,
            navigateBack = {}
        )

        DashboardScreen(
            state = viewModel.uiState,
            listener = viewModel,
            showPage = {
                when (it) {
                    TabItem.Id.APPOINTMENTS -> appointmentsScreen()
                    TabItem.Id.BUSINESS -> businessScreen()
                    TabItem.Id.SETTINGS -> settingsScreen()
                }
            }
        )
    }
}

@Composable
private fun HandleNavigation(
    viewModel: DashboardViewModel,
    navigateBack: () -> Unit
) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            else -> navigateBack()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}