package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.functions.brightness
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.filter
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scale
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkKind
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.style.CssStyleScope
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms

private const val BUTTON_TRANSITION_MS = 150
private const val HOVER_BRIGHTNESS = 1.12
private const val PRESSED_SCALE = 0.97

enum class ButtonLinkVariant {
    FILLED,
    TONAL,
}

val FilledButtonLinkVariant = LinkStyle.addVariant {
    val colors = colorMode.webColors()
    buttonLink(
        ColorToken.ButtonActive.resolve(colors),
        ColorToken.OnAction.resolve(colors),
        Modifier.filter(brightness(HOVER_BRIGHTNESS)),
    )
}

val TonalButtonLinkVariant = LinkStyle.addVariant {
    val colors = colorMode.webColors()
    buttonLink(
        ColorToken.Elevated.resolve(colors),
        ColorToken.PrimaryText.resolve(colors),
        Modifier.backgroundColor(ColorToken.Divider.resolve(colors)),
    )
}

@Composable
fun ButtonLink(
    text: StringDesc,
    path: String,
    variant: ButtonLinkVariant,
    modifier: Modifier = Modifier,
) {
    Link(path, text.localized(), modifier, variant = variant.style())
}

private fun ButtonLinkVariant.style(): CssStyleVariant<LinkKind> {
    return when (this) {
        ButtonLinkVariant.FILLED -> FilledButtonLinkVariant
        ButtonLinkVariant.TONAL -> TonalButtonLinkVariant
    }
}

private fun CssStyleScope.buttonLink(background: Color, content: Color, hoverEffect: Modifier) {
    val textColor = Modifier.color(content)
    base {
        Modifier
            .transition(
                Transition.of("background-color", BUTTON_TRANSITION_MS.ms),
                Transition.of("filter", BUTTON_TRANSITION_MS.ms),
                Transition.of("transform", BUTTON_TRANSITION_MS.ms),
            )
            .display(DisplayStyle.LegacyInlineFlex)
            .alignItems(AlignItems.Center)
            .minHeight(3.cssRem)
            .padding(leftRight = 1.5.cssRem)
            .borderRadius(1.cssRem)
            .backgroundColor(background)
            .fontSize(1.cssRem)
            .fontWeight(FontWeight.SemiBold)
            .textDecorationLine(TextDecorationLine.None)
    }
    link { textColor }
    visited { textColor }
    hover {
        textColor
            .textDecorationLine(TextDecorationLine.None)
            .then(hoverEffect)
    }
    active { Modifier.scale(PRESSED_SCALE) }
}
