package me.bookk.site.components.legal

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ListStyle
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.columnGap
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.listStyle
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.rowGap
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDate
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.JsDateLocalizer
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.legal.LegalHistoryEntry
import me.bookk.site.legal.LegalLanguageLink
import me.bookk.site.legal.LegalNotice
import me.bookk.site.legal.LegalPage
import me.bookk.site.legal.LegalPeriod
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ul

val LegalDocumentStyle = CssStyle.base {
    Modifier
        .maxWidth(48.cssRem)
        .gap(1.5.cssRem)
        .margin(top = 2.cssRem)
}

val LegalArticleStyle = CssStyle.base {
    Modifier
        .gap(1.cssRem)
}

val LegalLanguagesStyle = CssStyle.base {
    Modifier
        .columnGap(1.cssRem)
        .rowGap(0.5.cssRem)
        .flexWrap(FlexWrap.Wrap)
}

val LegalHistoryListStyle = CssStyle.base {
    Modifier
        .margin(0.cssRem)
        .padding(0.cssRem)
        .listStyle(ListStyle.None)
}

private val dateFormatter = JsDateLocalizer().forStyle(DateStyle.LONG)

@Composable
fun LegalDocumentContent(page: LegalPage) {
    Column(LegalDocumentStyle.toModifier()) {
        LegalLanguages(page.languages)
        page.notice?.let { LegalNoticeCard(it) }
        Article({ attr("lang", page.language.tag) }) {
            Column(LegalArticleStyle.toModifier()) {
                LegalMarkdownContent(page.blocks)
            }
        }
        LegalHistory(page.history)
    }
}

@Composable
private fun LegalLanguages(languages: List<LegalLanguageLink>) {
    val label = SiteRes.strings.site_language.desc().localized()
    Nav({ attr("aria-label", label) }) {
        Row(LegalLanguagesStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            languages.forEach { link ->
                LegalLink(
                    link.language.autonym.desc(),
                    link.path,
                    link.isSelected,
                    LabelMediumTextStyle.toModifier(),
                )
            }
        }
    }
}

@Composable
private fun LegalNoticeCard(notice: LegalNotice) {
    AppCard {
        TextBlock(notice.message(), BodyMediumTextStyle.toModifier())
        notice.link()?.let { (text, path) ->
            TextLink(
                text,
                path,
                BodyMediumTextStyle.toModifier().active(),
            )
        }
    }
}

@Composable
private fun LegalHistory(history: List<LegalHistoryEntry>) {
    Column(Modifier.gap(0.75.cssRem).margin(top = 1.cssRem)) {
        Heading(
            SiteRes.strings.site_legal_history.desc(),
            HeadingLevel.H2,
            HeadlineSmallTextStyle.toModifier(),
        )
        Ul(LegalHistoryListStyle.toModifier().toAttrs()) {
            history.forEach { entry ->
                Li(Modifier.margin(bottom = 0.5.cssRem).toAttrs()) { LegalHistoryItem(entry) }
            }
        }
    }
}

@Composable
private fun LegalHistoryItem(entry: LegalHistoryEntry) {
    LegalLink(
        SiteRes.strings.site_legal_version.format(entry.version),
        entry.path,
        entry.isSelected,
        BodyMediumTextStyle.toModifier(),
    )
    TextBlock(
        entry.period.description(),
        BodySmallTextStyle
            .toModifier()
            .secondary(),
    )
}

@Composable
private fun LegalLink(text: StringDesc, path: String, isSelected: Boolean, modifier: Modifier) {
    if (isSelected) {
        TextBlock(text, modifier.fontWeight(FontWeight.SemiBold))
    } else {
        TextLink(text, path, modifier.active())
    }
}

private fun LegalNotice.message(): StringDesc {
    return when (this) {
        is LegalNotice.Superseded -> SiteRes.strings.site_legal_notice_superseded.format(
            period.from.formatted(),
            period.until.formatted(),
        )

        is LegalNotice.Upcoming -> SiteRes.strings.site_legal_notice_upcoming.format(
            from.formatted(),
        )

        is LegalNotice.NewVersion -> SiteRes.strings.site_legal_notice_new_version.format(
            from.formatted(),
        )
    }
}

private fun LegalNotice.link(): Pair<StringDesc, String>? {
    return when (this) {
        is LegalNotice.Superseded -> SiteRes.strings.site_legal_view_current.desc() to currentPath

        is LegalNotice.Upcoming -> currentPath?.let {
            SiteRes.strings.site_legal_view_current.desc() to
                it
        }

        is LegalNotice.NewVersion -> SiteRes.strings.site_legal_view_new.desc() to path
    }
}

private fun LegalPeriod.description(): StringDesc {
    return when (this) {
        is LegalPeriod.InEffect -> SiteRes.strings.site_legal_in_effect_since.format(
            since.formatted(),
        )

        is LegalPeriod.Superseded -> SiteRes.strings.site_legal_in_effect_between.format(
            from.formatted(),
            until.formatted(),
        )

        is LegalPeriod.Upcoming -> SiteRes.strings.site_legal_takes_effect.format(from.formatted())
    }
}

private fun LocalDate.formatted(): String {
    return dateFormatter.format(this)
}
