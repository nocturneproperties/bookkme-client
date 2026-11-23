package me.bookk.site.designsystem.components

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.translateX
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.core.presentation.navigation.BackStackDirection
import org.jetbrains.compose.web.css.AnimationFillMode
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent

internal const val SCREEN_TRANSITION_MS = 320
private const val COVERED_SCREEN_OFFSET_PERCENT = -25

val ScreenEnterFromEndKeyframes = Keyframes {
    from { Modifier.translateX(100.percent) }
    to { Modifier.translateX(0.percent) }
}

val ScreenExitToEndKeyframes = Keyframes {
    from { Modifier.translateX(0.percent) }
    to { Modifier.translateX(100.percent) }
}

val ScreenEnterFromStartKeyframes = Keyframes {
    from { Modifier.translateX(COVERED_SCREEN_OFFSET_PERCENT.percent) }
    to { Modifier.translateX(0.percent) }
}

val ScreenExitToStartKeyframes = Keyframes {
    from { Modifier.translateX(0.percent) }
    to { Modifier.translateX(COVERED_SCREEN_OFFSET_PERCENT.percent) }
}

val ScreenEnterFromEndStyle = CssStyle {
    cssRule(MotionAllowed) { screenMotion(ScreenEnterFromEndKeyframes, colorMode) }
}

val ScreenExitToEndStyle = CssStyle {
    cssRule(MotionAllowed) { screenMotion(ScreenExitToEndKeyframes, colorMode) }
}

val ScreenEnterFromStartStyle = CssStyle {
    cssRule(MotionAllowed) { screenMotion(ScreenEnterFromStartKeyframes, colorMode) }
}

val ScreenExitToStartStyle = CssStyle {
    cssRule(MotionAllowed) { screenMotion(ScreenExitToStartKeyframes, colorMode) }
}

private fun screenMotion(keyframes: Keyframes, colorMode: ColorMode): Modifier {
    return Modifier.animation(
        keyframes.toAnimation(
            colorMode,
            duration = SCREEN_TRANSITION_MS.ms,
            timingFunction = DecelerateTiming,
            fillMode = AnimationFillMode.Both,
        ),
    )
}

internal fun enteringScreenStyle(direction: BackStackDirection): CssStyle<GeneralKind> {
    return when (direction) {
        BackStackDirection.FORWARD -> ScreenEnterFromEndStyle
        BackStackDirection.BACKWARD -> ScreenEnterFromStartStyle
    }
}

internal fun leavingScreenStyle(direction: BackStackDirection): CssStyle<GeneralKind> {
    return when (direction) {
        BackStackDirection.FORWARD -> ScreenExitToStartStyle
        BackStackDirection.BACKWARD -> ScreenExitToEndStyle
    }
}
