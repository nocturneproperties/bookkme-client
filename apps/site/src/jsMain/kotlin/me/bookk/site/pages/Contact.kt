package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.Band
import me.bookk.designsystem.components.DividedRows
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextLink
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.theme.BandTone
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import me.bookk.site.contact.ContactChannelRow
import me.bookk.site.contact.ContactContent
import me.bookk.site.landing.FeatureGrid
import me.bookk.site.layout.PageLayoutData
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

val ContactHeaderStyle = CssStyle.base {
    Modifier.gap(0.75.cssRem)
}

val LegalDocumentLinksStyle = CssStyle.base {
    Modifier
        .gap(1.cssRem)
        .margin(top = 3.cssRem)
}

@InitRoute
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(
        PageLayoutData(
            SiteRes.strings.site_contact_title.desc(),
            SiteRes.strings.site_contact_description.desc(),
            isFullWidth = true,
        ),
    )
}

@Page
@Layout(".layout.PageLayout")
@Composable
fun ContactPage() {
    Band {
        ContactHeader()
    }
    Band(tone = BandTone.ALTERNATE, isCompact = true) {
        Heading(
            SiteRes.strings.site_contact_before_title.desc(),
            HeadingLevel.H2,
            HeadlineSmallTextStyle
                .toModifier()
                .margin(bottom = 1.5.cssRem),
        )
        FeatureGrid(ContactContent.tips, wideColumns = 3)
    }
    Band {
        DividedRows {
            ContactContent.channels.forEach { ContactChannelRow(it) }
        }
        LegalDocumentLinks()
    }
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
                .maxWidth(40.cssRem)
                .secondary(),
        )
    }
}

@Composable
private fun LegalDocumentLinks() {
    Column(LegalDocumentLinksStyle.toModifier()) {
        Heading(
            SiteRes.strings.site_contact_legal_links_title.desc(),
            HeadingLevel.H2,
            HeadlineSmallTextStyle.toModifier(),
        )
        Row(
            Modifier
                .gap(1.5.cssRem)
                .flexWrap(FlexWrap.Wrap),
        ) {
            LegalDocument.entries.forEach {
                TextLink(
                    it.title.desc(),
                    WebLocalization.localize(it.path),
                    BodyMediumTextStyle
                        .toModifier()
                        .active(),
                )
            }
        }
    }
}
