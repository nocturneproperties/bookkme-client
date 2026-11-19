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
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.FeatureGrid
import me.bookk.site.components.widgets.ContactChannelRow
import me.bookk.site.content.ContactContent
import me.bookk.site.designsystem.components.Band
import me.bookk.site.designsystem.components.DividedRows
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.BandTone
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

val ContactHeaderStyle = CssStyle.base {
    Modifier.gap(0.75.cssRem)
}

val LegalDocumentsStyle = CssStyle.base {
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
@Layout(".components.layouts.PageLayout")
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
        LegalDocuments()
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
private fun LegalDocuments() {
    Column(LegalDocumentsStyle.toModifier()) {
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
