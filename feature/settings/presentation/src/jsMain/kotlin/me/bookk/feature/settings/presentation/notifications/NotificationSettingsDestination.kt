package me.bookk.feature.settings.presentation.notifications

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun NotificationSettingsDestination(webApp: TelegramWebApp, navigation: SettingsNavigation) {
    val viewModel = rememberViewModel<NotificationSettingsViewModel>()
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    NotificationSettingsScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            NotificationSettingsDestinations.Back -> navigation.onBack()
        }
    }
}
