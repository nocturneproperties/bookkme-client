package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSTimeNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.translateX
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.browser.window
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

private const val TRACK_INSET_PX = 3
private const val SWITCH_TRANSITION_MS = 250

class SegmentedLinkItem(
    val title: StringDesc,
    val path: String,
    val isSelected: Boolean,
)

val SegmentedLinksStyle = CssStyle.base {
    Modifier
        .position(Position.Relative)
        .display(DisplayStyle.Grid)
        .padding(TRACK_INSET_PX.px)
        .borderRadius(9999.px)
        .backgroundColor(ColorToken.Background.resolve(colorMode.webColors()))
}

val SegmentIndicatorStyle = CssStyle.base {
    Modifier
        .position(Position.Absolute)
        .top(TRACK_INSET_PX.px)
        .bottom(TRACK_INSET_PX.px)
        .left(TRACK_INSET_PX.px)
        .borderRadius(9999.px)
        .backgroundColor(ColorToken.Elevated.resolve(colorMode.webColors()))
}

val SegmentLinkVariant = LinkStyle.addVariant {
    val colors = colorMode.webColors()
    val textColor = Modifier.color(ColorToken.SecondaryText.resolve(colors))
    base { segmentBase() }
    link { textColor }
    visited { textColor }
    hover {
        Modifier
            .color(ColorToken.PrimaryText.resolve(colors))
            .textDecorationLine(TextDecorationLine.None)
    }
}

val SelectedSegmentLinkVariant = LinkStyle.addVariant {
    val textColor = Modifier.color(ColorToken.PrimaryText.resolve(colorMode.webColors()))
    base { segmentBase() }
    link { textColor }
    visited { textColor }
    hover { textColor.textDecorationLine(TextDecorationLine.None) }
}

@Composable
fun SegmentedLinks(
    label: StringDesc,
    items: List<SegmentedLinkItem>,
    modifier: Modifier = Modifier,
) {
    val groupLabel = label.localized()
    Div(
        SegmentedLinksStyle
            .toModifier()
            .styleModifier {
                property("grid-template-columns", "repeat(${items.size}, minmax(0, 1fr))")
            }
            .attrsModifier {
                attr("role", "group")
                attr("aria-label", groupLabel)
            }
            .then(modifier)
            .toAttrs(),
    ) {
        SegmentIndicator(items)
        items.forEach { SegmentLink(it) }
    }
}

@Composable
private fun SegmentIndicator(items: List<SegmentedLinkItem>) {
    val selectedIndex = items.indexOfFirst { it.isSelected }
    val duration = switchTransitionDuration()
    Box(
        SegmentIndicatorStyle
            .toModifier()
            .styleModifier {
                property("width", "calc((100% - ${TRACK_INSET_PX * 2}px) / ${items.size})")
            }
            .translateX((selectedIndex.coerceAtLeast(0) * 100).percent)
            .opacity(if (selectedIndex >= 0) 1 else 0)
            .transition(
                Transition.of("translate", duration, TransitionTimingFunction.Ease),
                Transition.of("opacity", duration, TransitionTimingFunction.Ease),
            ),
    )
}

@Composable
private fun SegmentLink(item: SegmentedLinkItem) {
    val variant = if (item.isSelected) SelectedSegmentLinkVariant else SegmentLinkVariant
    val current = if (item.isSelected) {
        Modifier.attrsModifier {
            attr("aria-current", "page")
        }
    } else {
        Modifier
    }
    Link(
        item.path,
        item.title.localized(),
        current.transition(
            Transition.of("color", switchTransitionDuration(), TransitionTimingFunction.Ease),
        ),
        variant = variant,
    )
}

private fun segmentBase(): Modifier {
    return Modifier
        .position(Position.Relative)
        .padding(topBottom = 0.375.cssRem, leftRight = 0.875.cssRem)
        .borderRadius(9999.px)
        .fontSize(0.85.cssRem)
        .fontWeight(FontWeight.Medium)
        .textAlign(TextAlign.Center)
        .whiteSpace(WhiteSpace.NoWrap)
        .textDecorationLine(TextDecorationLine.None)
}

private fun switchTransitionDuration(): CSSTimeNumericValue {
    return if (window.matchMedia(
            "(prefers-reduced-motion: reduce)",
        ).matches
    ) {
        0.ms
    } else {
        SWITCH_TRANSITION_MS.ms
    }
}
