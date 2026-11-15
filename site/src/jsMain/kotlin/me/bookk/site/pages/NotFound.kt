package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem

private const val PAGE_LAYOUT_ID = "me.bookk.site.components.layouts.PageLayout"

val NotFoundStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .flexGrow(1)
        .padding(topBottom = 4.cssRem)
}

@InitKobweb
fun initNotFoundPage(ctx: InitKobwebContext) {
    ctx.router.setErrorPage(PAGE_LAYOUT_ID, ::initNotFoundRoute) { NotFoundPage() }
}

private fun initNotFoundRoute(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_not_found_title.desc()))
}

@Composable
private fun NotFoundPage() {
    Box(NotFoundStyle.toModifier(), contentAlignment = Alignment.Center) {
        Heading(
            SiteRes.strings.site_not_found_message.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .textAlign(TextAlign.Center)
                .whiteSpace(WhiteSpace.PreLine),
        )
    }
}
