package me.bookk.feature.business.presentation.clients.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.clientsListScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Clients>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: BusinessDestination.Clients = it.toRoute()
        val args = ClientsListArgs(route.id)
        val viewModel: ClientsListViewModel = koinViewModel { parametersOf(args) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ClientsListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {

            }
        }
    }
}