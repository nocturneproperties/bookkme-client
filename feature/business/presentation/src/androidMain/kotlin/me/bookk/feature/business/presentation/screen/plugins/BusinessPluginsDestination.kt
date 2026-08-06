package me.bookk.feature.business.presentation.screen.plugins

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

fun NavGraphBuilder.pluginsScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Plugins>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>())
    ) {
        val entry = it.toRoute<BusinessDestination.Plugins>()
        val viewModel: BusinessPluginsViewModel = koinViewModel { parametersOf(entry.id) }

        CompositionLocalProvider(
            LocalNavigation provides navigation,
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            BusinessPluginsScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                BusinessPluginsDestinations.Back -> {
                    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        }
    }
}