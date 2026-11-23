package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.dvh
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.onAnimationEnd
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.browser.window
import kotlinx.coroutines.delay
import me.bookk.core.presentation.LocalViewModelStore
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.core.presentation.navigation.BackStackDirection
import me.bookk.core.presentation.navigation.BackStackEntry
import me.bookk.core.presentation.navigation.BackStackTransition
import me.bookk.core.presentation.navigation.LocalIsCurrentScreen
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.INSTANT
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions
import org.w3c.dom.events.Event

private const val TRANSITION_TIMEOUT_FACTOR = 3

val ScreenStackStyle = CssStyle.base {
    Modifier
        .position(Position.Relative)
        .fillMaxWidth()
        .styleModifier { property("overflow-x", "clip") }
}

val ScreenLayerStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(100.dvh)
        .backgroundColor(colorMode.webColors().background)
}

private class TransitionPlacement(
    val targetScrollTop: Double,
    val leavingScreenTop: Double,
)

@Composable
fun <R : Any> BackStackHost(stack: BackStack<R>, content: @Composable (R) -> Unit) {
    TrackScrollPosition(stack)
    val transition = stack.transition
    val placement = remember(transition) { transition?.placement() }
    FinishTransition(stack, transition)
    RestoreScrollPosition(placement)
    Div(ScreenStackStyle.toModifier().toAttrs()) {
        stack.visibleEntries.forEach { entry ->
            key(entry) {
                ScreenLayer(stack, entry, placement, content)
            }
        }
    }
}

@Composable
private fun <R : Any> ScreenLayer(
    stack: BackStack<R>,
    entry: BackStackEntry<R>,
    placement: TransitionPlacement?,
    content: @Composable (R) -> Unit,
) {
    val isCurrent = entry === stack.currentEntry
    Column(
        ScreenLayerStyle
            .toModifier()
            .then(stack.transitionModifier(entry, placement))
            .then(if (isCurrent) Modifier else Modifier.attrsModifier { attr("inert", "") }),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(
            LocalViewModelStore provides entry.viewModelStore,
            LocalIsCurrentScreen provides isCurrent,
        ) {
            content(entry.route)
        }
    }
}

@Composable
private fun <R : Any> BackStack<R>.transitionModifier(
    entry: BackStackEntry<R>,
    placement: TransitionPlacement?,
): Modifier {
    val transition = transition
    if (transition == null || placement == null) return Modifier.position(Position.Relative)
    val motion = if (entry === transition.to) {
        enteringScreenStyle(transition.direction)
    } else {
        leavingScreenStyle(transition.direction)
    }
    return motion
        .toModifier()
        .then(layerPosition(transition, entry, placement))
        .then(if (entry === transition.upperEntry) upperLayer(transition) else Modifier)
}

private fun <R : Any> layerPosition(
    transition: BackStackTransition<R>,
    entry: BackStackEntry<R>,
    placement: TransitionPlacement,
): Modifier {
    if (entry !== transition.from) return Modifier.position(Position.Relative)
    return Modifier
        .position(Position.Absolute)
        .top(placement.leavingScreenTop.px)
        .left(0.px)
}

private fun <R : Any> BackStack<R>.upperLayer(transition: BackStackTransition<R>): Modifier {
    return Modifier
        .zIndex(1)
        .onAnimationEnd { if (it.isOwnAnimation()) finish(transition) }
}

private fun <R : Any> BackStackTransition<R>.placement(): TransitionPlacement {
    val target = if (direction == BackStackDirection.FORWARD) 0.0 else to.scrollTop
    return TransitionPlacement(
        targetScrollTop = target,
        leavingScreenTop = target - from.scrollTop,
    )
}

private fun <R : Any> BackStack<R>.finish(finished: BackStackTransition<R>) {
    if (transition === finished) finishTransition()
}

@Composable
private fun <R : Any> FinishTransition(stack: BackStack<R>, transition: BackStackTransition<R>?) {
    LaunchedEffect(transition) {
        if (transition == null) return@LaunchedEffect
        if (!prefersReducedMotion()) {
            delay(
                SCREEN_TRANSITION_MS.toLong() * TRANSITION_TIMEOUT_FACTOR,
            )
        }
        stack.finish(transition)
    }
}

@Composable
private fun RestoreScrollPosition(placement: TransitionPlacement?) {
    DisposableEffect(placement) {
        if (placement != null) {
            window.scrollTo(
                ScrollToOptions(top = placement.targetScrollTop, behavior = ScrollBehavior.INSTANT),
            )
        }
        onDispose { }
    }
}

@Composable
private fun <R : Any> TrackScrollPosition(stack: BackStack<R>) {
    DisposableEffect(stack) {
        val listener: (Event) -> Unit = { stack.currentEntry.scrollTop = window.scrollY }
        window.addEventListener("scroll", listener)
        onDispose { window.removeEventListener("scroll", listener) }
    }
}
