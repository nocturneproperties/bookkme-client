package me.bookk.feature.services.presentation.group.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.services.presentation.LocalNavigation
import me.bookk.feature.services.presentation.ServicesDestination
import me.bookk.feature.services.presentation.ServicesNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.serviceGroupListScreen(navigation: ServicesNavigation) {
    composable<ServicesDestination.ServiceGroupList>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ServicesDestination.ServiceGroupList = it.toRoute()
        val viewModel: ServiceGroupListViewModel = koinViewModel { parametersOf(route.businessId) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ServiceGroupListScreen(viewModel.uiState, route.businessId)
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