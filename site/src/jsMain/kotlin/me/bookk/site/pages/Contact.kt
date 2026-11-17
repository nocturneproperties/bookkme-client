package me.bookk.site.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
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
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.widgets.ContactTopicList
import me.bookk.site.components.widgets.SupportTaskCard
import me.bookk.site.content.ContactContent
import me.bookk.site.content.SupportQuestion
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.Disclosure
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.designsystem.uistate.WebBooleanState
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

val ContactHeaderStyle = CssStyle {
    base {
        Modifier
            .gap(1.cssRem)
            .margin(top = 3.cssRem, bottom = 2.cssRem)
    }
    Breakpoint.MD { Modifier.margin(top = 4.cssRem, bottom = 3.cssRem) }
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
    val questions = remember { ContactContent.questions.associateWith { questionState(it) } }
    ContactHeader()
    BusinessNote()
    ContactSection(SiteRes.strings.site_contact_tasks_title.desc()) {
        SimpleGrid(numColumns(1, md = 2, lg = 4), Modifier.gap(1.cssRem)) {
            ContactContent.tasks.forEach { task ->
                SupportTaskCard(task) { questions.getValue(it).isChecked = true }
            }
        }
    }
    ContactSection(SiteRes.strings.site_contact_faq_title.desc()) {
        Questions(questions)
    }
    ContactSection(SiteRes.strings.site_contact_channels_title.desc()) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(2.cssRem),
        ) {
            ContactContent.channels.forEach { ContactTopicList(it) }
        }
    }
    ContactSection(SiteRes.strings.site_contact_checklist_title.desc()) {
        Checklist()
    }
    ContactSection(SiteRes.strings.site_contact_legal_links_title.desc()) {
        LegalLinks()
    }
}

private fun questionState(question: SupportQuestion): WebBooleanState {
    return WebBooleanState(text = question.question).apply {
        onCheckedChange = { isChecked = it }
    }
}

@Composable
private fun ContactHeader() {
    Column(ContactHeaderStyle.toModifier()) {
        Heading(
            SiteRes.strings.site_contact_headline.desc(),
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
private fun BusinessNote() {
    AppCard(Modifier.backgroundColor(ColorToken.ActionText.themed.toRgb().copyf(alpha = 0.12f))) {
        TextBlock(ContactContent.businessNote, BodyMediumTextStyle.toModifier())
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
private fun Questions(questions: Map<SupportQuestion, WebBooleanState>) {
    Column(Modifier.fillMaxWidth()) {
        questions.forEach { (question, state) ->
            Disclosure(state, Modifier.id(question.id)) {
                TextBlock(
                    question.answer,
                    BodyMediumTextStyle
                        .toModifier()
                        .maxWidth(44.cssRem)
                        .margin(top = 0.5.cssRem)
                        .color(ColorToken.SecondaryText.themed),
                )
            }
        }
    }
}

@Composable
private fun Checklist() {
    Column(Modifier.gap(1.cssRem)) {
        Ul(
            BodyMediumTextStyle
                .toModifier()
                .margin(0.cssRem)
                .padding(left = 1.25.cssRem)
                .toAttrs(),
        ) {
            ContactContent.checklist.forEach { Li { Text(it.localized()) } }
        }
        TextBlock(
            ContactContent.sensitiveNote,
            BodyMediumTextStyle
                .toModifier()
                .color(ColorToken.SecondaryText.themed),
        )
    }
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
                it.path,
                BodyMediumTextStyle
                    .toModifier()
                    .color(ColorToken.ActionText.themed),
            )
        }
    }
}
