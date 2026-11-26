package me.bookk.feature.authorization.presentation.screen.verifyemail

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignSelf
import library.telegram.TelegramBackButton
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TextField
import me.bookk.feature.authorization.presentation.screen.EmailChangeLayout

@Composable
internal fun VerifyEmailScreen(state: VerifyEmailState, webApp: TelegramWebApp) {
    TelegramBackButton(webApp, state.appBar.onBackClick)
    TelegramMainButton(webApp, state.confirmButton)
    EmailChangeLayout(state.appBar.title, state.codeSentMessage) {
        TextField(state.code)
        TextButton(state.resendButton, Modifier.alignSelf(AlignSelf.Start))
    }
}
