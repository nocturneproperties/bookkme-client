package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.functions.brightness
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.filter
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scale
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.selectors.disabled
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.webColors
import me.bookk.designsystem.uistate.ButtonState
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

private const val TEXT_HOVER_OPACITY = 0.75

val ActionButtonStyle = CssStyle {
    base {
        val colors = colorMode.webColors()
        Modifier
            .backgroundColor(colors.buttonActive)
            .color(colors.onAction)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .borderRadius(0.75.cssRem)
            .padding(topBottom = 0.75.cssRem, leftRight = 1.5.cssRem)
            .minHeight(3.cssRem)
            .fontSize(1.cssRem)
            .fontWeight(FontWeight.SemiBold)
            .cursor(Cursor.Pointer)
            .transition(
                Transition.of("filter", BUTTON_TRANSITION_MS.ms),
                Transition.of("opacity", BUTTON_TRANSITION_MS.ms),
                Transition.of("transform", BUTTON_TRANSITION_MS.ms),
            )
    }
    cssRule(":hover:not(:disabled)") { Modifier.filter(brightness(BUTTON_HOVER_BRIGHTNESS)) }
    cssRule(":active:not(:disabled)") { Modifier.scale(BUTTON_PRESSED_SCALE) }
    disabled {
        val colors = colorMode.webColors()
        Modifier
            .backgroundColor(colors.buttonInactive)
            .color(colors.actionTextDisabled)
            .cursor(Cursor.Default)
    }
}

val TextButtonStyle = CssStyle {
    base {
        val colors = colorMode.webColors()
        Modifier
            .backgroundColor(Colors.Transparent)
            .color(colors.actionText)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .padding(topBottom = 0.5.cssRem, leftRight = 0.75.cssRem)
            .fontSize(1.cssRem)
            .cursor(Cursor.Pointer)
            .transition(
                Transition.of("filter", BUTTON_TRANSITION_MS.ms),
                Transition.of("opacity", BUTTON_TRANSITION_MS.ms),
                Transition.of("transform", BUTTON_TRANSITION_MS.ms),
            )
    }
    cssRule(":hover:not(:disabled)") { Modifier.opacity(TEXT_HOVER_OPACITY) }
    disabled {
        Modifier
            .color(colorMode.webColors().actionTextDisabled)
            .cursor(Cursor.Default)
    }
}

@Composable
fun ActionButton(state: ButtonState, modifier: Modifier = Modifier) {
    StateButton(
        state,
        ActionButtonStyle
            .toModifier()
            .then(modifier),
    )
}

@Composable
fun TextButton(state: ButtonState, modifier: Modifier = Modifier) {
    StateButton(
        state,
        TextButtonStyle
            .toModifier()
            .then(modifier),
    )
}

@Composable
private fun StateButton(state: ButtonState, modifier: Modifier) {
    if (!state.isVisible) return
    Button(
        modifier.toAttrs {
            if (!state.isEnabled) disabled()
            onClick { state.onClick?.invoke() }
        },
    ) {
        Row(Modifier.gap(0.5.cssRem), verticalAlignment = Alignment.CenterVertically) {
            if (state.isLoading) {
                Spinner()
            } else {
                state.icon?.let { Image(it.fileUrl, modifier = Modifier.fontSize(1.em)) }
                Text(state.text.localized())
            }
        }
    }
}
