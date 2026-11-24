package library.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.themed

@Composable
fun rememberTelegramLaunch(): TelegramLaunchState {
    var launch by remember { mutableStateOf<TelegramLaunchState>(TelegramLaunchState.Loading) }
    val showSession = rememberShowSession()
    LaunchedEffect(Unit) {
        if (AppGlobals.isExporting) return@LaunchedEffect
        launch = TelegramSdk.launch()
        (launch as? TelegramLaunchState.InsideTelegram)?.let { showSession(it.webApp.session()) }
    }
    return launch
}

@Composable
fun TelegramHost(webApp: TelegramWebApp, content: @Composable () -> Unit) {
    FollowTelegramTheme(webApp)
    PaintTelegramChrome(webApp)
    content()
    LaunchedEffect(webApp) {
        webApp.ready()
        webApp.expand()
    }
}

@Composable
private fun rememberShowSession(): (TelegramSession) -> Unit {
    var colorMode by ColorMode.currentState
    return { session ->
        WebLocalization.show(session.language)
        colorMode = session.colorMode
    }
}

@Composable
private fun FollowTelegramTheme(webApp: TelegramWebApp) {
    val showSession = rememberShowSession()
    DisposableEffect(webApp) {
        val onThemeChanged = { showSession(webApp.session()) }
        webApp.onEvent(TelegramEvents.THEME_CHANGED, onThemeChanged)
        onDispose { webApp.offEvent(TelegramEvents.THEME_CHANGED, onThemeChanged) }
    }
}

@Composable
private fun PaintTelegramChrome(webApp: TelegramWebApp) {
    val background = ColorToken.Background.themed.toTelegramColor()
    LaunchedEffect(webApp, background) {
        webApp.setHeaderColor(background)
        webApp.setBackgroundColor(background)
        if (webApp.supports(TelegramFeature.BOTTOM_BAR_COLOR)) webApp.setBottomBarColor(background)
    }
}
