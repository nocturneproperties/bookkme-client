package me.bookk.feature.services.presentation.service.edit

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

internal fun NavGraphBuilder.editServiceScreen(navigation: ServicesNavigation) {
    composable<ServicesDestination.EditService>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val route: ServicesDestination.EditService = it.toRoute()
        val viewModel: EditServiceViewModel = koinViewModel { parametersOf(route.serviceId) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            EditServiceScreen(viewModel.uiState)
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    EditServiceDestination.Back -> navigation.onBack()
                }
            }
        }
    }
}
