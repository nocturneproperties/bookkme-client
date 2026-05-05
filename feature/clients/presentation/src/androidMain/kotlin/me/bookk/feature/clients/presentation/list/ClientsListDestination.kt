package me.bookk.feature.clients.presentation.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.clients.presentation.ClientsDestinations
import me.bookk.feature.clients.presentation.ClientsNavigation
import me.bookk.feature.clients.presentation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.clientsListScreen(navigation: ClientsNavigation) {
    composable<ClientsDestinations.Clients>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ClientsDestinations.Clients = it.toRoute()
        val args = ClientsListArgs(route.id)
        val viewModel: ClientsListViewModel = koinViewModel { parametersOf(args) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ClientsListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    is ClientsListDestination.AddClient -> {}
                    is ClientsListDestination.ClientDetails -> {}
                    ClientsListDestination.Back -> navigation.onBack()
                }
            }
        }
    }
}