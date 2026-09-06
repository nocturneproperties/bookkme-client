package me.bookk.feature.clients.presentation.list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.clients.presentation.ClientsDestinations
import me.bookk.feature.clients.presentation.ClientsNavigation
import me.bookk.feature.clients.presentation.LocalNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.clientsListScreen(navigation: ClientsNavigation) {
    composable<ClientsDestinations.Clients> {
        val viewModel: ClientsListViewModel = koinViewModel()
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ClientsListScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    is ClientsListDestination.AddClient -> navigation.toAddClient(it.businessId)
                    is ClientsListDestination.ClientDetails -> navigation.toClientDetails(it.clientId)
                    ClientsListDestination.Back -> navigation.onBack()
                }
            }
        }
    }
}