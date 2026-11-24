package me.bookk.designsystem.components

import kotlinx.browser.window
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.events.SyntheticAnimationEvent

internal val MotionAllowed = CSSMediaQuery.MediaFeature(
    "prefers-reduced-motion",
    StylePropertyValue("no-preference"),
)

internal val DecelerateTiming = AnimationTimingFunction.cubicBezier(0.2, 0.8, 0.2, 1.0)

internal fun prefersReducedMotion(): Boolean {
    return window.matchMedia("(prefers-reduced-motion: reduce)").matches
}

internal fun SyntheticAnimationEvent.isOwnAnimation(): Boolean {
    return nativeEvent.target == nativeEvent.currentTarget
}
