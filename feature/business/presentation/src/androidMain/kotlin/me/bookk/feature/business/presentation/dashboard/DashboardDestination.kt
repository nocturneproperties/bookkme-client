package me.bookk.feature.business.presentation.dashboard

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.dashboardScreen() {
    composable<BusinessDestination.Dashboard> {
        val viewModel: BusinessDashboardViewModel = koinViewModel()
        val listener = DashboardEventListener(
            onItemClicked = viewModel::onItemClick
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