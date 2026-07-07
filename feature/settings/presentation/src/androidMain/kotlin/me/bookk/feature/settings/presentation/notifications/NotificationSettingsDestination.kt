package me.bookk.feature.settings.presentation.notifications

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.notificationSettingsScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.Notifications> {
        val viewModel: NotificationSettingsViewModel = koinViewModel()

        CompositionLocalProvider(LocalNavigation provides navigation) {
            SendLifecycleEventsTo(viewModel)
            NotificationSettingsScreen(viewModel.uiState)
        }
    }
}
