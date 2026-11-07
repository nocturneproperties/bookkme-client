package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.privacySettingsScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.Privacy> {
        val viewModel: PrivacySettingsViewModel = koinViewModel()
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            PrivacySettingsScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                PrivacySettingsDestinations.Back -> navigation.navigateBack()
            }
        }
    }
}
