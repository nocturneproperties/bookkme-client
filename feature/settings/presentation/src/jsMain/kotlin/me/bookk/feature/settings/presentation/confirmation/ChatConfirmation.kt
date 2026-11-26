package me.bookk.feature.settings.presentation.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import library.telegram.TelegramWebApp
import library.telegram.askWriteAccess
import me.bookk.designsystem.components.InfoNote

@Composable
internal fun ChatConfirmationNotice(state: ChatConfirmationState) {
    state.notice?.let { InfoNote(it) }
}

@Composable
internal fun AskTelegramWriteAccess(webApp: TelegramWebApp, state: ChatConfirmationState) {
    LaunchedEffect(webApp, state.isWriteAccessRequested) {
        if (state.isWriteAccessRequested) {
            state.onWriteAccessAnswered(webApp.askWriteAccess())
        }
    }
}
