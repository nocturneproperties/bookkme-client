package me.bookk.client.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.isExporting
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.client.di.initClientApp
import me.bookk.client.layout.ClientPageHead
import me.bookk.client.resources.ClientRes
import me.bookk.client.telegram.TelegramMiniApp
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.designsystem.resources.WebLocalization
import org.jetbrains.compose.web.css.vh

val TelegramPageStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(100.vh)
}

@InitRoute
fun initIndexPage(ctx: InitRouteContext) {
    WebLocalization.show(preferredLanguage())
    initClientApp()
}

private fun preferredLanguage(): AppLanguage {
    if (AppGlobals.isExporting) return AppLanguage.DEFAULT
    return WebLocalization.preferredLanguage()
}

@Page
@Composable
fun IndexPage() {
    ClientPageHead(
        title = ClientRes.strings.client_telegram_title.desc(),
        description = ClientRes.strings.client_telegram_description.desc(),
    )
    Column(
        TelegramPageStyle.toModifier(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TelegramMiniApp()
    }
}
