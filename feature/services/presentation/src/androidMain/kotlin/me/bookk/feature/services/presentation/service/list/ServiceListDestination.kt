package me.bookk.feature.services.presentation.service.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.services.presentation.LocalNavigation
import me.bookk.feature.services.presentation.ServicesDestination
import me.bookk.feature.services.presentation.ServicesNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.serviceListScreen(navigation: ServicesNavigation) {
    composable<ServicesDestination.Services> {
        val viewModel: ServiceListViewModel = koinViewModel()
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ServiceListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    ServiceListDestination.Back -> navigation.onBack()
                    is ServiceListDestination.ServiceDetails -> {}
                    is ServiceListDestination.AddService -> navigation.toCreateService(it.businessId)
                    ServiceListDestination.ServiceGroups -> navigation.toServiceGroupList()
                }
            }
        }
    }
}