package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
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
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.widgets.ContactTopicList
import me.bookk.site.content.ContactContent
import me.bookk.site.content.SiteNavigation
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.components.TitledCard
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

val ContactHeaderStyle = CssStyle {
    base {
        Modifier
            .gap(1.cssRem)
            .margin(top = 3.cssRem, bottom = 2.cssRem)
    }
    Breakpoint.MD { Modifier.margin(top = 4.cssRem, bottom = 2.cssRem) }
}

val ContactSectionStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(1.cssRem)
        .margin(top = 3.cssRem)
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
    ContactHints()
    Column(
        ContactSectionStyle
            .toModifier()
            .gap(2.5.cssRem),
    ) {
        ContactContent.channels.forEach { ContactTopicList(it) }
    }
    SensitiveNote()
    ContactSection(SiteRes.strings.site_contact_legal_links_title.desc()) {
        LegalLinks()
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
    }
}

@Composable
private fun ContactHints() {
    SimpleGrid(
        numColumns(1, md = 2),
        Modifier
            .fillMaxWidth()
            .gap(1.cssRem),
    ) {
        ContactHint(SiteRes.strings.site_contact_guides_question.desc()) {
            TextLink(
                SiteRes.strings.site_contact_guides_link.desc(),
                WebLocalization.localize(SiteNavigation.GUIDES_PATH),
                BodyMediumTextStyle
                    .toModifier()
                    .active(),
            )
        }
        ContactHint(ContactContent.appointmentQuestion) {
            TextBlock(
                ContactContent.appointmentAnswer,
                BodyMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
    }
}

@Composable
private fun ContactHint(question: StringDesc, answer: @Composable () -> Unit) {
    TitledCard(question, Modifier.gap(0.375.cssRem)) {
        answer()
    }
}

@Composable
private fun ContactSection(title: StringDesc, content: @Composable () -> Unit) {
    Column(ContactSectionStyle.toModifier()) {
        Heading(title, HeadingLevel.H2, HeadlineSmallTextStyle.toModifier())
        content()
    }
}

@Composable
private fun SensitiveNote() {
    TextBlock(
        ContactContent.sensitiveNote,
        BodyMediumTextStyle
            .toModifier()
            .margin(top = 2.5.cssRem)
            .secondary(),
    )
}

@Composable
private fun LegalLinks() {
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
