package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
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

enum class InitialsAvatarSize(
    val diameterRem: Double,
    val fontSizeRem: Double,
    val lineHeightRem: Double,
) {
    REGULAR(diameterRem = 2.75, fontSizeRem = 1.0, lineHeightRem = 1.25),
    LARGE(diameterRem = 4.5, fontSizeRem = 1.5, lineHeightRem = 1.75),
}

@Composable
fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: InitialsAvatarSize = InitialsAvatarSize.REGULAR,
) {
    Box(
        TextTileStyle
            .toModifier()
            .size(size.diameterRem.cssRem)
            .borderRadius(50.percent)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Span(
            TextTileAccentStyle
                .toModifier()
                .fontSize(size.fontSizeRem.cssRem)
                .lineHeight(size.lineHeightRem.cssRem)
                .attrsModifier { attr("aria-hidden", "true") }
                .toAttrs(),
        ) { Text(initials) }
    }
}
