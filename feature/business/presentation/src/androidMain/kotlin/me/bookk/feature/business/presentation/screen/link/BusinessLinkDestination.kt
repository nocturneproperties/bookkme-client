package me.bookk.feature.business.presentation.screen.link

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

internal fun NavGraphBuilder.businessLinkScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.BusinessLink>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>()),
    ) {
        val entry = it.toRoute<BusinessDestination.BusinessLink>()
        val viewModel: BusinessLinkViewModel = koinViewModel { parametersOf(entry.id) }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            BusinessLinkScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                BusinessLinkDestinations.Back -> {
                    navigation.goBack()
                }
            }
        }
    }
}
