package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.di.initClientApp
import me.bookk.site.layout.PageHead
import me.bookk.site.layout.PageLayoutData
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.navigation.preferredLanguage
import me.bookk.site.resources.SiteRes
import me.bookk.site.telegram.TelegramMiniApp
import org.jetbrains.compose.web.css.vh

val TelegramPageStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(100.vh)
}

@InitRoute
fun initTelegramPage(ctx: InitRouteContext) {
    WebLocalization.show(preferredLanguage())
    initClientApp()
}

@Page
@Composable
fun TelegramPage() {
    PageHead(
        PageLayoutData(
            title = SiteRes.strings.site_telegram_title.desc(),
            description = SiteRes.strings.site_telegram_description.desc(),
            isIndexed = false,
        ),
        SiteNavigation.TELEGRAM_PATH,
    )
    Column(
        TelegramPageStyle.toModifier(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TelegramMiniApp()
    }
}
