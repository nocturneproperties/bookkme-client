package me.bookk.client.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramHost
import library.telegram.TelegramLaunchState
import library.telegram.TelegramSdk
import library.telegram.TelegramWebApp
import library.telegram.rememberTelegramLaunch
import me.bookk.client.navigation.ClientLink
import me.bookk.client.resources.ClientRes
import me.bookk.core.config.SiteUrl
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapDestination
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInDestination
import org.koin.mp.KoinPlatform

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
        ClientBootstrapDestination(
            loginContent = {
                TelegramSignInDestination(
                    webApp,
                    messageLayout = { TelegramMessageLayout(it) },
                    formLayout = { TelegramFormLayout(it) },
                )
            },
            mainContent = { TelegramMainContent(webApp) },
        )
    }
}

@Composable
private fun OutsideTelegramMessage() {
    val siteUrl = remember { KoinPlatform.getKoin().get<SiteUrl>() }
    TelegramMessage(
        title = ClientRes.strings.client_telegram_outside_title.desc(),
        body = ClientRes.strings.client_telegram_outside_body.desc(),
        link = ClientLink.siteClients(
            ClientRes.strings.client_telegram_outside_link.desc(),
            siteUrl,
        ),
    )
}
