package library.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun TelegramSettingsButton(webApp: TelegramWebApp, onClick: () -> Unit) {
    if (!webApp.supports(TelegramFeature.SETTINGS_BUTTON)) return
    val settingsButton = webApp.settingsButton
    val currentOnClick by rememberUpdatedState(onClick)
    DisposableEffect(settingsButton) {
        val onPressed = { currentOnClick() }
        settingsButton.onClick(onPressed)
        settingsButton.show()
        onDispose {
            settingsButton.offClick(onPressed)
            settingsButton.hide()
        }
    }
}
