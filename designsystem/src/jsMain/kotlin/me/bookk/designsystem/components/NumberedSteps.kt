package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.listStyle
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ol
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

val NumberedStepsStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .margin(0.px)
        .padding(leftRight = 1.cssRem, topBottom = 0.25.cssRem)
        .listStyle(ListStyleType.None)
        .borderRadius(CardCornerRadius)
        .textAlign(TextAlign.Start)
        .backgroundColor(colorMode.webColors().elevated)
}

val NumberedStepStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .gap(0.75.cssRem)
            .minHeight(3.cssRem)
            .borderBottom(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
    }
    cssRule(":last-child") {
        Modifier.borderBottom(
            0.px,
            LineStyle.None,
            ColorToken.Divider.resolve(colorMode.webColors()),
        )
    }
}

@Composable
fun NumberedSteps(steps: List<StringDesc>, modifier: Modifier = Modifier) {
    Ol(
        NumberedStepsStyle
            .toModifier()
            .then(modifier)
            .toAttrs(),
    ) {
        steps.forEachIndexed { index, step ->
            Li(NumberedStepStyle.toModifier().toAttrs()) {
                StepNumber(index + 1)
                Span(BodyLargeTextStyle.toModifier().toAttrs()) { Text(step.localized()) }
            }
        }
    }
}

@Composable
private fun StepNumber(number: Int) {
    Box(
        TextTileStyle
            .toModifier()
            .size(1.5.cssRem)
            .borderRadius(50.percent)
            .attrsModifier { attr("aria-hidden", "true") },
        contentAlignment = Alignment.Center,
    ) {
        Span(
            TextTileAccentStyle
                .toModifier()
                .fontSize(0.8125.cssRem)
                .fontWeight(FontWeight.Bold)
                .toAttrs(),
        ) { Text(number.toString()) }
    }
}
