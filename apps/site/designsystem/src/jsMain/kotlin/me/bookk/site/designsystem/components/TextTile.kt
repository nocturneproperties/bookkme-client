package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextTransform
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.textTransform
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val TEXT_TILE_TINT_ALPHA = 0.12f

val TextTileStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .flexShrink(0)
        .borderRadius(0.75.cssRem)
        .color(colors.primaryText)
        .backgroundColor(
            ColorToken.ActionText
                .resolve(colors)
                .toRgb()
                .copyf(alpha = TEXT_TILE_TINT_ALPHA),
        )
}

val TextTileAccentStyle = CssStyle.base {
    Modifier
        .color(ColorToken.ActionText.resolve(colorMode.webColors()))
        .fontWeight(FontWeight.Bold)
}

@Composable
fun DateTile(weekday: String, day: String, modifier: Modifier = Modifier) {
    Column(
        TextTileStyle
            .toModifier()
            .width(3.5.cssRem)
            .height(4.cssRem)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Span(
            TextTileAccentStyle
                .toModifier()
                .fontSize(0.75.cssRem)
                .lineHeight(1.cssRem)
                .textTransform(TextTransform.Uppercase)
                .toAttrs(),
        ) { Text(weekday) }
        Span(
            Modifier
                .fontSize(1.5.cssRem)
                .lineHeight(1.75.cssRem)
                .fontWeight(FontWeight.Bold)
                .toAttrs(),
        ) { Text(day) }
    }
}
