package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.CSSLengthNumericValue
import com.varabyte.kobweb.compose.css.CSSTimeNumericValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.css.dvh
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backdropFilter
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.icons.CloseIcon
import com.varabyte.kobweb.silk.components.icons.HamburgerIcon
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.browser.document
import kotlinx.browser.window
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLElement

private const val SURFACE_ALPHA = 0.75f

val NavigationBarStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .position(Position.Sticky)
        .top(0.px)
        .zIndex(1)
}

val NavigationBarSurfaceStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .position(Position.Absolute)
        .top(0.px)
        .left(0.px)
        .right(0.px)
        .boxSizing(BoxSizing.ContentBox)
        .overflow(Overflow.Hidden)
        .backgroundColor(
            ColorToken.Elevated
                .resolve(colors)
                .toRgb()
                .copyf(alpha = SURFACE_ALPHA),
        )
        .backdropFilter(saturate(180.percent), blur(20.px))
        .borderBottom(1.px, LineStyle.Solid, colors.divider)
}

val NavigationBarMenuItemsStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .padding(leftRight = 1.cssRem, top = 0.5.cssRem, bottom = 1.5.cssRem)
        .gap(1.25.cssRem)
}

@Composable
fun NavigationBar(
    height: CSSLengthNumericValue,
    isExpanded: Boolean,
    onCollapse: () -> Unit,
    menuBreakpoint: Breakpoint,
    bar: @Composable () -> Unit,
    menu: @Composable ColumnScope.() -> Unit,
) {
    CollapseOnRouteOrLayoutChange(menuBreakpoint, onCollapse)
    LockPageScroll(isExpanded)
    Box(
        NavigationBarStyle
            .toModifier()
            .height(height)
            .onKeyDown { if (it.key == "Escape") onCollapse() },
    ) {
        Column(
            NavigationBarSurfaceStyle
                .toModifier()
                .height(if (isExpanded) 100.dvh else height)
                .transition(navigationBarTransition("height")),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(height)
                    .flexShrink(0),
                contentAlignment = Alignment.Center,
            ) {
                bar()
            }
            NavigationBarMenu(isExpanded, menuBreakpoint, menu)
        }
    }
}

@Composable
fun NavigationMenuButton(
    title: StringDesc,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        description = title,
        modifier = modifier.attrsModifier { attr("aria-expanded", isExpanded.toString()) },
    ) {
        if (isExpanded) CloseIcon() else HamburgerIcon()
    }
}

@Composable
private fun NavigationBarMenu(
    isExpanded: Boolean,
    breakpoint: Breakpoint,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        NavigationBarMenuItemsStyle
            .toModifier()
            .displayUntil(breakpoint)
            .attrsModifier { if (!isExpanded) attr("inert", "") },
    ) {
        content()
    }
}

@OptIn(DelicateApi::class)
@Composable
private fun CollapseOnRouteOrLayoutChange(menuBreakpoint: Breakpoint, onCollapse: () -> Unit) {
    val path = rememberPageContext().route.path
    val isCompact = rememberBreakpoint() < menuBreakpoint
    LaunchedEffect(path, isCompact) {
        onCollapse()
    }
}

@Composable
private fun LockPageScroll(isLocked: Boolean) {
    DisposableEffect(isLocked) {
        val root = document.documentElement?.unsafeCast<HTMLElement>()
        if (isLocked && root != null) lockScroll(root)
        onDispose {
            root?.style?.removeProperty("overflow")
            root?.style?.removeProperty("scrollbar-gutter")
        }
    }
}

private fun lockScroll(root: HTMLElement) {
    if (window.innerWidth > root.clientWidth) root.style.setProperty("scrollbar-gutter", "stable")
    root.style.setProperty("overflow", "hidden")
}

private fun navigationBarTransition(property: String): Transition {
    return Transition.of(property, navigationBarTransitionDuration(), TransitionTimingFunction.Ease)
}

private fun navigationBarTransitionDuration(): CSSTimeNumericValue {
    return if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) 0.ms else 250.ms
}
