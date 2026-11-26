package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun DeleteAccountDestination(webApp: TelegramWebApp, navigation: SettingsNavigation) {
    val viewModel = rememberViewModel<DeleteAccountViewModel>()
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    DeleteAccountScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            DeleteAccountNavigationDestination.Back -> {
                navigation.onBack()
            }
        }
    }
}
