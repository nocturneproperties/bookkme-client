package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.confirmaction.ConfirmTelegramAction
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun DeleteAccountDestination(
    webApp: TelegramWebApp,
    navigation: SettingsNavigation,
    confirmsInTelegram: Boolean = false,
) {
    val viewModel = rememberViewModel<DeleteAccountViewModel>()
    if (confirmsInTelegram) {
        ConfirmTelegramAction(webApp, ConfirmableAction.DELETE_ACCOUNT)
    }
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
