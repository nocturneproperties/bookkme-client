package me.bookk.feature.services.presentation.group.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.services.presentation.LocalNavigation
import me.bookk.feature.services.presentation.ServicesDestination
import me.bookk.feature.services.presentation.ServicesNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.serviceGroupListScreen(navigation: ServicesNavigation) {
    composable<ServicesDestination.ServiceGroupList> {
        val viewModel: ServiceGroupListViewModel = koinViewModel()
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ServiceGroupListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    is ServiceGroupListDestination.AddGroup -> {}
                    ServiceGroupListDestination.Back -> navigation.onBack()
                }
            }
        }
    }
}