package me.bookk.site.legal.ui

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BorderCollapse
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.VerticalAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderCollapse
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scrollMarginBlock
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.verticalAlign
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.HorizontalDivider
import me.bookk.designsystem.components.TextLink
import me.bookk.designsystem.components.TextList
import me.bookk.designsystem.components.TextListType
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import me.bookk.site.legal.markdown.MarkdownBlock
import me.bookk.site.legal.markdown.MarkdownInline
import me.bookk.site.navigation.SiteNavigation
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Table
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr

val LegalHeadingStyle = CssStyle.base {
    Modifier
        .margin(top = 1.cssRem)
        .scrollMarginBlock(start = 4.cssRem)
}

val LegalTableWrapperStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .overflow(overflowX = Overflow.Auto, overflowY = Overflow.Visible)
}

val LegalTableStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .borderCollapse(BorderCollapse.Collapse)
}

val LegalTableCellStyle = CssStyle.base {
    Modifier
        .border(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
        .padding(0.5.cssRem)
        .textAlign(TextAlign.Start)
        .verticalAlign(VerticalAlign.Top)
}

val LegalTableHeaderStyle = CssStyle.base {
    Modifier
        .backgroundColor(ColorToken.Elevated.resolve(colorMode.webColors()))
        .fontWeight(FontWeight.SemiBold)
}

@Composable
fun LegalMarkdownContent(blocks: List<MarkdownBlock>) {
    blocks.forEach { block ->
        when (block) {
            is MarkdownBlock.Heading -> LegalHeading(block)

            is MarkdownBlock.Paragraph -> LegalParagraph(block.content)

            is MarkdownBlock.BulletList -> TextList(block.items) { LegalInlines(it) }

            is MarkdownBlock.OrderedList -> TextList(block.items, type = TextListType.ORDERED) {
                LegalInlines(it)
            }

            MarkdownBlock.ThematicBreak -> HorizontalDivider(
                Modifier.margin(topBottom = 0.5.cssRem),
            )

            is MarkdownBlock.Table -> LegalTable(block)
        }
    }
}

@Composable
private fun LegalHeading(heading: MarkdownBlock.Heading) {
    val text = heading.content.plainText().desc()
    val anchor = heading.anchor?.let { Modifier.id(it) } ?: Modifier
    val section = LegalHeadingStyle.toModifier().then(anchor)
    when (heading.level) {
        1 -> Heading(text, HeadingLevel.H1, HeadlineLargeTextStyle.toModifier().then(anchor))
        2 -> Heading(text, HeadingLevel.H2, HeadlineSmallTextStyle.toModifier().then(section))
        else -> Heading(text, HeadingLevel.H3, TitleMediumTextStyle.toModifier().then(section))
    }
}

@Composable
private fun LegalParagraph(content: List<MarkdownInline>) {
    P(BodyLargeTextStyle.toModifier().toAttrs()) {
        LegalInlines(content)
    }
}

@Composable
private fun LegalTable(table: MarkdownBlock.Table) {
    Div(LegalTableWrapperStyle.toModifier().toAttrs()) {
        Table(
            BodyMediumTextStyle
                .toModifier()
                .then(LegalTableStyle.toModifier())
                .toAttrs(),
        ) {
            Thead {
                Tr {
                    table.header.forEach { cell ->
                        Th(
                            LegalTableCellStyle
                                .toModifier()
                                .then(LegalTableHeaderStyle.toModifier())
                                .toAttrs(),
                        ) { LegalInlines(cell) }
                    }
                }
            }
            Tbody {
                table.rows.forEach { row ->
                    Tr {
                        row.forEach { cell ->
                            Td(LegalTableCellStyle.toModifier().toAttrs()) { LegalInlines(cell) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalInlines(content: List<MarkdownInline>) {
    content.forEach { inline ->
        when (inline) {
            is MarkdownInline.Text -> Text(inline.value)

            is MarkdownInline.Strong -> Span(Modifier.fontWeight(FontWeight.SemiBold).toAttrs()) {
                LegalInlines(inline.content)
            }

            is MarkdownInline.Link -> TextLink(
                inline.label.desc(),
                WebLocalization.localize(inline.url.toSitePath()),
                Modifier.active(),
            )
        }
    }
}

private fun List<MarkdownInline>.plainText(): String {
    return joinToString("") { inline ->
        when (inline) {
            is MarkdownInline.Text -> inline.value
            is MarkdownInline.Strong -> inline.content.plainText()
            is MarkdownInline.Link -> inline.label
        }
    }
}

private fun String.toSitePath(): String {
    val origin = "${SiteNavigation.ORIGIN}/"
    if (!startsWith(origin)) return this
    return "/" + removePrefix(origin)
}
