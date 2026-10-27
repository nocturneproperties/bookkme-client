package me.bookk.feature.clients.presentation.details

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

internal fun NavGraphBuilder.clientDetailsScreen(navigation: ClientsNavigation) {
    composable<ClientsDestinations.ClientDetails>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ClientsDestinations.ClientDetails = it.toRoute()
        val viewModel: ClientDetailsViewModel = koinViewModel { parametersOf(route.id) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ClientDetailsScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    ClientDetailsDestination.Back -> navigation.onBack()
                    is ClientDetailsDestination.Edit -> navigation.toEditClient(it.id)
                }
            }
        }
    }
}