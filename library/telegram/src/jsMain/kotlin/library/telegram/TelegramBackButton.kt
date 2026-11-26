package library.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import me.bookk.core.presentation.navigation.LocalIsCurrentScreen
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.uistate.AppBarState

@Composable
fun TelegramAppTopBar(webApp: TelegramWebApp, state: AppBarState) {
    val hasNativeBackButton = webApp.supports(TelegramFeature.BACK_BUTTON)
    if (hasNativeBackButton) {
        TelegramBackButton(webApp, state.onBackClick?.takeIf { state.isVisible })
    }
    AppTopBar(state, showsBackButton = !hasNativeBackButton)
}

@Composable
fun TelegramBackButton(webApp: TelegramWebApp, onBackClick: (() -> Unit)?) {
    if (LocalIsCurrentScreen.current) {
        val backButton = webApp.backButton
        val onClick by rememberUpdatedState(onBackClick)
        DisposableEffect(backButton) {
            val onPressed = { onClick?.invoke() ?: Unit }
            backButton.onClick(onPressed)
            onDispose {
                backButton.offClick(onPressed)
                backButton.hide()
            }
        }
        val isVisible = onBackClick != null
        LaunchedEffect(backButton, isVisible) {
            if (isVisible) backButton.show() else backButton.hide()
        }
    }
}
