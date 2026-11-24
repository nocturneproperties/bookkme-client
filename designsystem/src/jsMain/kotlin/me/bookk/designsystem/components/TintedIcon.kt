package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem

private const val ICON_TILE_TINT_ALPHA = 0.12f

@Composable
fun TintedIcon(image: ImageResource, color: ColorToken, modifier: Modifier = Modifier) {
    val mask = "url(\"${image.fileUrl}\") center / contain no-repeat"
    Box(
        Modifier
            .size(1.5.cssRem)
            .flexShrink(0)
            .backgroundColor(color.themed)
            .styleModifier {
                property("mask", mask)
                property("-webkit-mask", mask)
            }
            .attrsModifier { attr("aria-hidden", "true") }
            .then(modifier),
    )
}

val IconTileStyle = CssStyle.base {
    Modifier
        .size(2.75.cssRem)
        .flexShrink(0)
        .borderRadius(0.875.cssRem)
        .backgroundColor(
            ColorToken.ActionText
                .resolve(colorMode.webColors())
                .toRgb()
                .copyf(alpha = ICON_TILE_TINT_ALPHA),
        )
}

@Composable
fun IconTile(image: ImageResource, modifier: Modifier = Modifier) {
    Box(
        IconTileStyle
            .toModifier()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        TintedIcon(image, ColorToken.ActionText)
    }
}
