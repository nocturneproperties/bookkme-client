package me.bookk.feature.settings.presentation.deletionbanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramEvents
import library.telegram.TelegramWebApp
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.AlertStrip
import me.bookk.designsystem.components.BottomStripLayout

@Composable
fun AccountDeletionNotice(webApp: TelegramWebApp, content: @Composable () -> Unit) {
    val viewModel = rememberViewModel<AccountDeletionBannerViewModel>()
    val state = viewModel.uiState
    CheckWhenActivated(webApp, viewModel)
    ObserveTelegramNotifications(webApp, state.notifications)
    BottomStripLayout(
        isStripShown = state.isVisible,
        strip = {
            AlertStrip(
                text = state.message,
                action = state.cancelButton,
                icon = DesignSystem.images.material_auto_delete,
            )
        },
        content = content,
    )
}

@Composable
private fun CheckWhenActivated(webApp: TelegramWebApp, viewModel: AccountDeletionBannerViewModel) {
    DisposableEffect(webApp, viewModel) {
        val onActivated = { viewModel.onAppResumed() }
        onActivated()
        webApp.onEvent(TelegramEvents.ACTIVATED, onActivated)
        onDispose { webApp.offEvent(TelegramEvents.ACTIVATED, onActivated) }
    }
}
