package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextLink
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import me.bookk.site.layout.PageLayoutData
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem

val GuidesComingSoonStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .flexGrow(1)
        .gap(1.cssRem)
        .padding(topBottom = 4.cssRem)
        .textAlign(TextAlign.Center)
}

@InitRoute
fun initGuidesPage(ctx: InitRouteContext) {
    ctx.data.add(
        PageLayoutData(
            SiteRes.strings.site_guides_title.desc(),
            SiteRes.strings.site_guides_description.desc(),
        ),
    )
}

@Page
@Layout(".layout.PageLayout")
@Composable
fun GuidesPage() {
    Column(
        GuidesComingSoonStyle.toModifier(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Heading(
            SiteRes.strings.site_guides_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle.toModifier(),
        )
        TextBlock(
            SiteRes.strings.site_guides_coming_soon.desc(),
            BodyLargeTextStyle
                .toModifier()
                .maxWidth(32.cssRem)
                .secondary(),
        )
        TextLink(
            SiteRes.strings.site_guides_contact_link.desc(),
            WebLocalization.localize(SiteNavigation.CONTACT_PATH),
            BodyMediumTextStyle
                .toModifier()
                .active(),
        )
    }
}
