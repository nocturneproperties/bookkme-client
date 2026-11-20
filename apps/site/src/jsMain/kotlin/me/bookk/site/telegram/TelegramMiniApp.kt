package me.bookk.site.telegram

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramHost
import library.telegram.TelegramLaunchState
import library.telegram.TelegramSdk
import library.telegram.TelegramWebApp
import library.telegram.rememberTelegramLaunch
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInScreen
import me.bookk.site.navigation.SiteLink
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes

@InitKobweb
fun initTelegramSdk(ctx: InitKobwebContext) {
    TelegramSdk.rememberLaunchHash()
}

@Composable
fun TelegramMiniApp() {
    when (val launch = rememberTelegramLaunch()) {
        TelegramLaunchState.Loading -> Unit
        TelegramLaunchState.OutsideTelegram -> OutsideTelegramMessage()
        is TelegramLaunchState.InsideTelegram -> InsideTelegram(launch.webApp)
    }
}

@Composable
private fun InsideTelegram(webApp: TelegramWebApp) {
    TelegramHost(webApp) {
        TelegramMessageLayout {
            TelegramSignInScreen(webApp) {
                TelegramMessageBody(SiteRes.strings.site_telegram_coming_soon_body.desc())
            }
        }
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
