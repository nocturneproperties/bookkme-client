package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.simple.InfoLine
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button

private val InfoCardLineHorizontalPadding = 1.cssRem

val InfoCardLineStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .minHeight(3.cssRem)
        .padding(topBottom = 0.75.cssRem, leftRight = InfoCardLineHorizontalPadding)
        .gap(1.cssRem)
}

val InfoCardOnlyLineStyle = CssStyle.base {
    Modifier.padding(topBottom = 1.cssRem, leftRight = InfoCardLineHorizontalPadding)
}

@Composable
fun InfoCard(lines: List<InfoLine>, modifier: Modifier = Modifier) {
    if (lines.isNotEmpty()) {
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(0.px)
                .gap(0.px)
                .then(modifier),
        ) {
            lines.forEachIndexed { index, line ->
                InfoCardLine(line, isOnlyLine = lines.size == 1)
                if (index != lines.lastIndex) InsetDivider()
            }
        }
    }
}

@Composable
private fun InsetDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(leftRight = 1.cssRem),
    ) {
        HorizontalDivider()
    }
}

@Composable
fun InfoCardLine(line: InfoLine, isOnlyLine: Boolean = false) {
    val onClick = line.onClick
    if (onClick == null) {
        InfoCardLineContent(line, isOnlyLine)
    } else {
        Button(
            ListRowButtonStyle
                .toModifier()
                .padding(0.px)
                .toAttrs { onClick { onClick() } },
        ) {
            InfoCardLineContent(line, isOnlyLine)
        }
    }
}

@Composable
private fun InfoCardLineContent(line: InfoLine, isOnlyLine: Boolean) {
    Row(
        InfoCardLineStyle
            .toModifier()
            .then(if (isOnlyLine) InfoCardOnlyLineStyle.toModifier() else Modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextBlock(
            line.title,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px)
                .flexShrink(0),
        )
        TextBlock(
            line.value,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px)
                .flexGrow(1)
                .minWidth(0.px)
                .textAlign(TextAlign.End)
                .overflowWrap(OverflowWrap.Anywhere)
                .secondary(),
        )
    }
}
