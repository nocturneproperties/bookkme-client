package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.widgets.ContactCard
import me.bookk.site.content.ContactContent
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem

val ContactHeaderStyle = CssStyle {
    base {
        Modifier
            .gap(1.cssRem)
            .margin(top = 3.cssRem, bottom = 2.cssRem)
    }
    Breakpoint.MD { Modifier.margin(top = 4.cssRem, bottom = 3.cssRem) }
}

@InitRoute
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_contact_title.desc()))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ContactPage() {
    ContactHeader()
    SimpleGrid(numColumns(1, md = 2), Modifier.gap(1.cssRem)) {
        ContactContent.channels.forEach { ContactCard(it) }
    }
    ContactNotes()
}

@Composable
private fun ContactHeader() {
    Column(ContactHeaderStyle.toModifier()) {
        Heading(
            SiteRes.strings.site_contact_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle.toModifier(),
        )
        TextBlock(
            SiteRes.strings.site_contact_subtitle.desc(),
            BodyLargeTextStyle
                .toModifier()
                .maxWidth(44.cssRem)
                .color(ColorToken.SecondaryText.themed),
        )
    }
}

@Composable
private fun ContactNotes() {
    Column(
        Modifier
            .gap(0.5.cssRem)
            .margin(top = 2.cssRem),
    ) {
        ContactContent.notes.forEach {
            TextBlock(
                it,
                BodyMediumTextStyle
                    .toModifier()
                    .color(ColorToken.SecondaryText.themed),
            )
        }
    }
}
