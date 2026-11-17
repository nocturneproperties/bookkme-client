package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Hr

val HorizontalDividerStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .margin(0.px)
        .border(0.px, LineStyle.None)
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

val VerticalDividerStyle = CssStyle.base {
    Modifier
        .width(1.px)
        .height(1.em)
        .flexShrink(0)
        .backgroundColor(ColorToken.Divider.resolve(colorMode.webColors()))
}

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    Hr(
        HorizontalDividerStyle
            .toModifier()
            .then(modifier)
            .toAttrs(),
    )
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        VerticalDividerStyle
            .toModifier()
            .then(modifier),
    )
}
