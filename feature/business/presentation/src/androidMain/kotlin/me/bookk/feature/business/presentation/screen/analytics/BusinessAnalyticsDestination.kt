package me.bookk.feature.business.presentation.screen.analytics

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.analyticsScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Analytics> {
        val viewModel: BusinessAnalyticsViewModel = koinViewModel()

        CompositionLocalProvider(
            LocalNavigation provides navigation,
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            BusinessAnalyticsScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                BusinessAnalyticsDestinations.Back -> {
                    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        }
    }
}
