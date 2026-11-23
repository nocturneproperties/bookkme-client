package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scale
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.WebDesignSystem
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button

private const val PRESSABLE_HOVER_OPACITY = 0.85

private val PressableBase = Modifier
    .fillMaxWidth()
    .border(0.px, LineStyle.None, Colors.Transparent)
    .fontFamily("inherit")
    .fontSize(1.cssRem)
    .textAlign(TextAlign.Start)
    .cursor(Cursor.Pointer)
    .transition(
        Transition.of("opacity", BUTTON_TRANSITION_MS.ms),
        Transition.of("transform", BUTTON_TRANSITION_MS.ms),
    )

val CardButtonStyle = CssStyle {
    base {
        val colors = colorMode.webColors()
        PressableBase
            .backgroundColor(colors.elevated)
            .color(colors.primaryText)
            .borderRadius(1.cssRem)
            .padding(0.75.cssRem)
    }
    cssRule(":hover") { Modifier.opacity(PRESSABLE_HOVER_OPACITY) }
    cssRule(":active") { Modifier.scale(BUTTON_PRESSED_SCALE) }
}

val ListRowButtonStyle = CssStyle {
    base {
        PressableBase
            .backgroundColor(Colors.Transparent)
            .color(colorMode.webColors().primaryText)
            .minHeight(4.cssRem)
            .padding(leftRight = 1.cssRem, topBottom = 0.625.cssRem)
    }
    cssRule(":hover") { Modifier.opacity(PRESSABLE_HOVER_OPACITY) }
}

@Composable
fun CardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        CardButtonStyle
            .toModifier()
            .then(modifier)
            .toAttrs { onClick { onClick() } },
    ) {
        Row(Modifier.fillMaxWidth().gap(0.75.cssRem)) { content() }
    }
}

@Composable
fun ListRowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    endIcon: ImageResource = WebDesignSystem.images.material_chevron_right,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        ListRowButtonStyle
            .toModifier()
            .then(modifier)
            .toAttrs { onClick { onClick() } },
    ) {
        Row(
            Modifier.fillMaxWidth().gap(0.75.cssRem),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
            TintedIcon(
                endIcon,
                ColorToken.SecondaryText,
                Modifier.size(1.25.cssRem),
            )
        }
    }
}
