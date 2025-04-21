package me.bookk.feature.settings.presentation.passkey

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.passkeyScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.Passkey> {
        val viewModel: PasskeyViewModel = koinViewModel()

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalPasskeyEventListener provides PasskeyEventListener(
                onDeletePasskeyClick = viewModel::onDeletePasskeyClick,
                onAddPasskeyClick = viewModel::onAddPasskeyClick,
                onRefresh = viewModel::getPasskeyList
            )
        ) {
            SendLifecycleEventsTo(viewModel)
            PasskeyScreen(viewModel.uiState)
        }
    }
}