package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.translateY
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.browser.window
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

private const val CARD_RAIL_BLEED_REM = 1.0
private const val CARD_RAIL_ARROW_INSET_REM = 0.25
private const val CARD_RAIL_ARROW_ATTRIBUTE = "data-card-rail-arrow"
private const val CARD_RAIL_ARROW_FADE_MS = 150

private val PointerWithHover = CSSMediaQuery.Raw("(hover: hover) and (pointer: fine)")

val CardRailFrameStyle = CssStyle {
    base {
        Modifier
            .position(Position.Relative)
            .margin(leftRight = (-CARD_RAIL_BLEED_REM).cssRem)
            .styleModifier { property("width", "calc(100% + ${2 * CARD_RAIL_BLEED_REM}rem)") }
    }
    cssRule(":hover [$CARD_RAIL_ARROW_ATTRIBUTE]") { Modifier.opacity(1) }
    cssRule(":focus-within [$CARD_RAIL_ARROW_ATTRIBUTE]") { Modifier.opacity(1) }
}

val CardRailStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(leftRight = CARD_RAIL_BLEED_REM.cssRem, topBottom = 0.25.cssRem)
            .gap(0.75.cssRem)
            .styleModifier {
                property("overflow-x", "auto")
                property("overscroll-behavior-x", "contain")
                property("scroll-padding-inline", CARD_RAIL_BLEED_REM.cssRem)
                property("scrollbar-width", "none")
            }
    }
    cssRule("::-webkit-scrollbar") { Modifier.display(DisplayStyle.None) }
    cssRule(MotionAllowed) { Modifier.scrollBehavior(ScrollBehavior.Smooth) }
}

val CardRailSnappingStyle = CssStyle {
    cssRule(" > *") { Modifier.styleModifier { property("scroll-snap-stop", "always") } }
}

val CardRailItemStyle = CssStyle.base {
    Modifier
        .flexShrink(0)
        .styleModifier { property("scroll-snap-align", "start") }
}

val CardRailArrowStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.None)
            .position(Position.Absolute)
            .top(50.percent)
            .translateY((-50).percent)
            .zIndex(1)
            .opacity(0)
            .transition(Transition.of("opacity", CARD_RAIL_ARROW_FADE_MS.ms))
    }
    cssRule(PointerWithHover) { Modifier.display(DisplayStyle.Block) }
}

val CardRailArrowButtonStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .size(2.25.cssRem)
        .setVariable(ButtonVars.BackgroundDefaultColor, ColorToken.Elevated.resolve(colors))
        .boxShadow(0.px, 2.px, 8.px, color = Colors.Black.copyf(alpha = 0.18f))
}

private enum class CardRailDirection(
    val sign: Int,
) {
    BACK(-1),
    FORWARD(1),
}

@Composable
fun CardRail(
    modifier: Modifier = Modifier,
    snapsToEachCard: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    var track by remember { mutableStateOf<HTMLElement?>(null) }
    var position by remember { mutableStateOf(GalleryPosition.START) }
    val measure: () -> Unit = {
        track?.let { position = GalleryPosition.of(it.railMetrics()) }
    }
    ObserveWindowResize(measure)
    SideEffect { window.requestAnimationFrame { measure() } }
    Div(
        CardRailFrameStyle
            .toModifier()
            .then(modifier)
            .toAttrs { onMouseEnter { measure() } },
    ) {
        Row(
            CardRailStyle
                .toModifier()
                .then(if (snapsToEachCard) CardRailSnappingStyle.toModifier() else Modifier)
                .styleModifier { property("scroll-snap-type", snapType(snapsToEachCard)) }
                .attrsModifier {
                    ref { element ->
                        track = element.unsafeCast<HTMLElement>()
                        onDispose { track = null }
                    }
                    onScroll { measure() }
                },
        ) {
            content()
        }
        CardRailArrows(position) { direction ->
            track?.let { it.scrollBy(direction.sign * it.cardStep(), 0.0) }
        }
    }
}

@Composable
private fun CardRailArrows(position: GalleryPosition, onScroll: (CardRailDirection) -> Unit) {
    if (position.canGoBack) {
        CardRailArrow(
            DesignSystem.images.material_chevron_left,
            DesignSystem.strings.card_rail_previous.desc(),
            Modifier.left(CARD_RAIL_ARROW_INSET_REM.cssRem),
        ) { onScroll(CardRailDirection.BACK) }
    }
    if (position.canGoForward) {
        CardRailArrow(
            DesignSystem.images.material_chevron_right,
            DesignSystem.strings.card_rail_next.desc(),
            Modifier.right(CARD_RAIL_ARROW_INSET_REM.cssRem),
        ) { onScroll(CardRailDirection.FORWARD) }
    }
}

@Composable
private fun CardRailArrow(
    icon: ImageResource,
    label: StringDesc,
    placement: Modifier,
    onClick: () -> Unit,
) {
    Div(
        CardRailArrowStyle
            .toModifier()
            .then(placement)
            .toAttrs { attr(CARD_RAIL_ARROW_ATTRIBUTE, "") },
    ) {
        IconButton(onClick, label, CardRailArrowButtonStyle.toModifier()) {
            TintedIcon(icon, ColorToken.PrimaryText, Modifier.size(1.5.cssRem))
        }
    }
}

private fun snapType(snapsToEachCard: Boolean): String {
    return if (snapsToEachCard) "x mandatory" else "x proximity"
}

private fun HTMLElement.railMetrics(): GalleryMetrics {
    return GalleryMetrics(
        scrollLeft = scrollLeft,
        viewportWidth = clientWidth.toDouble(),
        scrollWidth = scrollWidth.toDouble(),
        itemStep = cardStep(),
        itemCount = children.length,
    )
}

private fun HTMLElement.cardStep(): Double {
    val first = children.item(0)?.unsafeCast<HTMLElement>()
    val second = children.item(1)?.unsafeCast<HTMLElement>()
    return if (first != null && second != null) {
        (second.offsetLeft - first.offsetLeft).toDouble()
    } else {
        clientWidth.toDouble()
    }
}
