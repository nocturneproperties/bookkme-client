package me.bookk.feature.settings.presentation.confirmaction

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.rememberViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun ConfirmTelegramAction(webApp: TelegramWebApp, action: ConfirmableAction) {
    val viewModel = rememberViewModel<ConfirmActionViewModel>(action) {
        parametersOf(action, webApp.initData)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
}
