package me.bookk.feature.clients.presentation.edit

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

internal fun NavGraphBuilder.editClientScreen(navigation: ClientsNavigation) {
    composable<ClientsDestinations.EditClient>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ClientsDestinations.EditClient = it.toRoute()
        val viewModel: EditClientViewModel = koinViewModel { parametersOf(route.id) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            EditClientScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    EditClientDestination.Back -> navigation.onBack()
                    EditClientDestination.Deleted -> {
                        navigation.onBack()
                        navigation.onBack()
                    }
                }
            }
        }
    }
}
