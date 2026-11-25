package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.alignSelf
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.themed
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val STATUS_PILL_TINT_ALPHA = 0.16f

val StatusPillStyle = CssStyle.base {
    Modifier
        .alignSelf(AlignSelf.FlexStart)
        .gap(0.375.cssRem)
        .padding(
            top = 0.1875.cssRem,
            bottom = 0.1875.cssRem,
            left = 0.5.cssRem,
            right = 0.625.cssRem,
        )
        .borderRadius(1.cssRem)
        .fontSize(0.8125.cssRem)
        .lineHeight(1.125.cssRem)
        .fontWeight(FontWeight.SemiBold)
}

val StatusLabelStyle = CssStyle.base {
    Modifier
        .alignSelf(AlignSelf.FlexStart)
        .gap(0.375.cssRem)
        .fontSize(0.8125.cssRem)
        .lineHeight(1.125.cssRem)
        .fontWeight(FontWeight.SemiBold)
}

val StatusTextStyle = CssStyle.base {
    Modifier
        .flexShrink(0)
        .fontSize(0.9375.cssRem)
        .lineHeight(1.25.cssRem)
        .fontWeight(FontWeight.SemiBold)
}

@Composable
fun StatusPill(text: StringDesc, color: ColorToken, modifier: Modifier = Modifier) {
    val tint = color.themed
    Row(
        StatusPillStyle
            .toModifier()
            .backgroundColor(tint.toRgb().copyf(alpha = STATUS_PILL_TINT_ALPHA))
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot(tint)
        Span { Text(text.localized()) }
    }
}

@Composable
fun StatusLabel(text: StringDesc, color: ColorToken, modifier: Modifier = Modifier) {
    Row(
        StatusLabelStyle
            .toModifier()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot(color.themed)
        Span { Text(text.localized()) }
    }
}

@Composable
private fun StatusDot(color: Color) {
    Box(
        Modifier
            .size(0.5.cssRem)
            .flexShrink(0)
            .borderRadius(50.percent)
            .backgroundColor(color),
    )
}

@Composable
fun StatusText(text: StringDesc, color: ColorToken, modifier: Modifier = Modifier) {
    Span(
        StatusTextStyle
            .toModifier()
            .color(color.themed)
            .then(modifier)
            .toAttrs(),
    ) {
        Text(text.localized())
    }
}
