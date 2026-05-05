package me.bookk.feature.clients.presentation.create

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

internal fun NavGraphBuilder.createClientScreen(navigation: ClientsNavigation) {
    composable<ClientsDestinations.CreateClient>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ClientsDestinations.CreateClient = it.toRoute()
        val viewModel: CreateClientViewModel = koinViewModel { parametersOf(route.businessId) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            CreateClientScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    CreateClientDestination.Back -> navigation.onBack()
                    is CreateClientDestination.Details -> {}
                }
            }
        }
    }
}