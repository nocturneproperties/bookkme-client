package me.bookk.feature.authorization.presentation.screen.changeemail

import androidx.compose.runtime.Composable
import library.telegram.TelegramBackButton
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.designsystem.components.TextField
import me.bookk.feature.authorization.presentation.screen.EmailChangeLayout

@Composable
internal fun ChangeEmailScreen(state: ChangeEmailState, webApp: TelegramWebApp) {
    TelegramBackButton(webApp, state.appBar.onBackClick)
    TelegramMainButton(webApp, state.continueButton)
    EmailChangeLayout(state.appBar.title, state.explanation) {
        TextField(state.email)
    }
}
