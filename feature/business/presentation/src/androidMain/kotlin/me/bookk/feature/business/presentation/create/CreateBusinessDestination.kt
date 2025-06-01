package me.bookk.feature.business.presentation.create

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.createBusinessScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Create> {
        val viewModel: CreateBusinessViewModel = koinViewModel()
        val listener = CreateBusinessEventListener(
            onNameChanged = viewModel::onBusinessNameChanged,
            onCreateClick = viewModel::onCreateClick
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalCreateBusinessEventListener provides listener
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            CreateBusinessScreen(viewModel.uiState)
        }
    }
}