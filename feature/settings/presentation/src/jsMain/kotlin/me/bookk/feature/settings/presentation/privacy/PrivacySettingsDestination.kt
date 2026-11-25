package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun PrivacySettingsDestination(webApp: TelegramWebApp, navigation: SettingsNavigation) {
    val viewModel = rememberViewModel<PrivacySettingsViewModel>()
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    PrivacySettingsScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            PrivacySettingsDestinations.Back -> navigation.onBack()
        }
    }
}
