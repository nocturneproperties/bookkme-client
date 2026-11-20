package me.bookk.site.telegram

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
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.navigation.SiteLink
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes

@Composable
fun TelegramMiniApp() {
    when (val launch = rememberTelegramLaunch()) {
        TelegramLaunchState.Loading -> Unit
        TelegramLaunchState.OutsideTelegram -> OutsideTelegramMessage()
        is TelegramLaunchState.InsideTelegram -> InsideTelegramMessage(launch.webApp)
    }
}

@Composable
private fun rememberTelegramLaunch(): TelegramLaunchState {
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
private fun rememberShowSession(): (TelegramSession) -> Unit {
    var colorMode by ColorMode.currentState
    return { session ->
        WebLocalization.show(session.language)
        colorMode = session.colorMode
    }
}

@Composable
private fun InsideTelegramMessage(webApp: TelegramWebApp) {
    FollowTelegramTheme(webApp)
    PaintTelegramChrome(webApp)
    TelegramMessage(
        title = SiteRes.strings.site_telegram_coming_soon_title.desc(),
        body = SiteRes.strings.site_telegram_coming_soon_body.desc(),
    )
    LaunchedEffect(webApp) {
        webApp.ready()
        webApp.expand()
    }
}

@Composable
private fun OutsideTelegramMessage() {
    TelegramMessage(
        title = SiteRes.strings.site_telegram_outside_title.desc(),
        body = SiteRes.strings.site_telegram_outside_body.desc(),
        link = SiteLink(
            SiteRes.strings.site_telegram_outside_link.desc(),
            SiteNavigation.CLIENTS_PATH,
        ),
    )
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
    }
}
