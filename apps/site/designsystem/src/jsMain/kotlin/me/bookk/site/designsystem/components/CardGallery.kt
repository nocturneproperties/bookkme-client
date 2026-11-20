package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.tabIndex
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.browser.window
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

private val GalleryInlinePadding = "max(1rem, calc((100% - $ContentMaxWidth) / 2 + 1rem))"

val GalleryTrackStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .display(DisplayStyle.Flex)
            .gap(1.25.cssRem)
            .padding(topBottom = 0.25.cssRem)
            .styleModifier {
                property("overflow-x", "auto")
                property("overscroll-behavior-x", "contain")
                property("scroll-snap-type", "x mandatory")
                property("scrollbar-width", "none")
                property("padding-inline", GalleryInlinePadding)
                property("scroll-padding-inline", GalleryInlinePadding)
            }
    }
    cssRule("::-webkit-scrollbar") { Modifier.display(DisplayStyle.None) }
    cssRule(
        CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("no-preference")),
    ) {
        Modifier.scrollBehavior(ScrollBehavior.Smooth)
    }
}

val GalleryItemStyle = CssStyle.base {
    Modifier
        .flexShrink(0)
        .styleModifier {
            property("width", "min(85%, 30rem)")
            property("scroll-snap-align", "start")
        }
}

val GalleryControlsStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .margin(top = 1.5.cssRem)
        .justifyContent(JustifyContent.SpaceBetween)
}

val GalleryDotStyle = CssStyle.base {
    Modifier
        .size(0.5.cssRem)
        .borderRadius(9999.px)
        .backgroundColor(ColorToken.Divider.resolve(colorMode.webColors()))
        .transition(Transition.group(listOf("width", "background-color"), 250.ms))
}

val SelectedGalleryDotStyle = CssStyle.base {
    Modifier
        .width(1.5.cssRem)
        .backgroundColor(ColorToken.SecondaryText.resolve(colorMode.webColors()))
}

val GalleryPaddleStyle = CssStyle.base {
    Modifier
        .size(2.5.cssRem)
        .setVariable(
            ButtonVars.BackgroundDefaultColor,
            ColorToken.ButtonInactive.resolve(colorMode.webColors()),
        )
}

@Composable
fun CardGallery(
    itemCount: Int,
    label: StringDesc,
    navigation: GalleryNavigation,
    item: @Composable (index: Int, modifier: Modifier) -> Unit,
) {
    var track by remember { mutableStateOf<HTMLElement?>(null) }
    var position by remember { mutableStateOf(GalleryPosition.START) }
    val measure: () -> Unit = {
        track?.let { position = GalleryPosition.of(it.galleryMetrics(itemCount)) }
    }
    val trackLabel = label.localized()
    ObserveWindowResize(measure)
    Column(Modifier.fillMaxWidth()) {
        Div(
            GalleryTrackStyle
                .toModifier()
                .tabIndex(0)
                .attrsModifier { attr("aria-label", trackLabel) }
                .toAttrs {
                    ref { element ->
                        track = element
                        window.requestAnimationFrame { measure() }
                        onDispose { track = null }
                    }
                    onScroll { measure() }
                },
        ) {
            repeat(itemCount) { item(it, GalleryItemStyle.toModifier()) }
        }
        SectionContainer {
            GalleryControls(position, itemCount, navigation) { direction ->
                track?.let { it.scrollBy(direction * it.itemStep(), 0.0) }
            }
        }
    }
}

class GalleryNavigation(
    val previousLabel: StringDesc,
    val nextLabel: StringDesc,
    val previousIcon: ImageResource,
    val nextIcon: ImageResource,
)

@Composable
private fun ObserveWindowResize(onResize: () -> Unit) {
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { onResize() }
        window.addEventListener("resize", listener)
        onDispose { window.removeEventListener("resize", listener) }
    }
}

@Composable
private fun GalleryControls(
    position: GalleryPosition,
    itemCount: Int,
    navigation: GalleryNavigation,
    onScrollBy: (Int) -> Unit,
) {
    Row(GalleryControlsStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
        GalleryDots(position.index, itemCount)
        Row(Modifier.gap(0.75.cssRem)) {
            GalleryPaddle(navigation.previousIcon, navigation.previousLabel, position.canGoBack) {
                onScrollBy(-1)
            }
            GalleryPaddle(navigation.nextIcon, navigation.nextLabel, position.canGoForward) {
                onScrollBy(1)
            }
        }
    }
}

@Composable
private fun GalleryDots(selectedIndex: Int, itemCount: Int) {
    Row(
        Modifier
            .gap(0.5.cssRem)
            .attrsModifier { attr("aria-hidden", "true") },
    ) {
        repeat(itemCount) { index ->
            val selected = if (index ==
                selectedIndex
            ) {
                SelectedGalleryDotStyle.toModifier()
            } else {
                Modifier
            }
            Box(
                GalleryDotStyle
                    .toModifier()
                    .then(selected),
            )
        }
    }
}

@Composable
private fun GalleryPaddle(
    icon: ImageResource,
    label: StringDesc,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick, label, GalleryPaddleStyle.toModifier(), enabled) {
        TintedIcon(icon, ColorToken.PrimaryText, Modifier.size(1.5.cssRem))
    }
}

private fun HTMLElement.galleryMetrics(itemCount: Int): GalleryMetrics {
    return GalleryMetrics(
        scrollLeft = scrollLeft,
        viewportWidth = clientWidth.toDouble(),
        scrollWidth = scrollWidth.toDouble(),
        itemStep = itemStep(),
        itemCount = itemCount,
    )
}

private fun HTMLElement.itemStep(): Double {
    val first = children.item(0)?.unsafeCast<HTMLElement>()
    val second = children.item(1)?.unsafeCast<HTMLElement>()
    return if (first != null &&
        second != null
    ) {
        (second.offsetLeft - first.offsetLeft).toDouble()
    } else {
        0.0
    }
}
