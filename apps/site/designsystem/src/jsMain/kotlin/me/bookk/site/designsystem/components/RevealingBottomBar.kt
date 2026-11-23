package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.PointerEvents
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.pointerEvents
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.translateY
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

private const val BOTTOM_BAR_ENTER_MS = 280
private const val BOTTOM_BAR_EXIT_MS = 220

private val EnterTiming = TransitionTimingFunction.cubicBezier(0.2, 0.8, 0.2, 1.0)
private val ExitTiming = TransitionTimingFunction.cubicBezier(0.4, 0.0, 1.0, 1.0)

val RevealingBottomBarStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .position(Position.Sticky)
        .bottom(0.px)
        .zIndex(1)
        .overflow(Overflow.Hidden)
}

val RevealingBottomBarPanelStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .display(DisplayStyle.Flex)
        .justifyContent(JustifyContent.Center)
        .backgroundColor(colors.background)
        .borderTop(1.px, LineStyle.Solid, colors.divider)
}

val RevealingBottomBarEnterStyle = CssStyle {
    cssRule(MotionAllowed) {
        Modifier.transition(Transition.of("translate", BOTTOM_BAR_ENTER_MS.ms, EnterTiming))
    }
}

val RevealingBottomBarExitStyle = CssStyle {
    cssRule(MotionAllowed) {
        Modifier.transition(Transition.of("translate", BOTTOM_BAR_EXIT_MS.ms, ExitTiming))
    }
}

private class ShownValue<T : Any>(
    var value: T?,
)

@Composable
fun <T : Any> RevealingBottomBar(value: T?, content: @Composable (T) -> Unit) {
    val shown = remember { ShownValue<T>(null) }
    if (value != null) shown.value = value
    val isShown = value != null
    Div(
        RevealingBottomBarStyle
            .toModifier()
            .then(if (isShown) Modifier else hiddenBar())
            .toAttrs(),
    ) {
        Div(
            RevealingBottomBarPanelStyle
                .toModifier()
                .then(panelMotion(isShown))
                .translateY(if (isShown) 0.percent else 100.percent)
                .toAttrs(),
        ) {
            shown.value?.let { content(it) }
        }
    }
}

@Composable
private fun panelMotion(isShown: Boolean): Modifier {
    if (isShown) return RevealingBottomBarEnterStyle.toModifier()
    return RevealingBottomBarExitStyle.toModifier()
}

private fun hiddenBar(): Modifier {
    return Modifier
        .pointerEvents(PointerEvents.None)
        .attrsModifier {
            attr("inert", "")
            attr("aria-hidden", "true")
        }
}
