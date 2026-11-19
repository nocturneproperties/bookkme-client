package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.theme.BandTone
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.LabelSmallTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.surfaceColors
import me.bookk.site.designsystem.theme.surfaces
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

enum class MediaFrame {
    PHONE,
    BROWSER,
    PANEL,
}

val PhoneFrameStyle = CssStyle.base {
    Modifier
        .width(15.cssRem)
        .padding(0.55.cssRem)
        .borderRadius(2.75.cssRem)
        .backgroundColor(ColorToken.Divider.resolve(colorMode.webColors()))
}

val PhoneScreenStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .overflow(Overflow.Hidden)
        .height(31.cssRem)
        .borderRadius(2.2.cssRem)
        .backgroundColor(ColorToken.Background.resolve(colorMode.webColors()))
}

val BrowserFrameStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .borderRadius(0.875.cssRem)
        .overflow(Overflow.Hidden)
        .border(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colors))
        .backgroundColor(ColorToken.Elevated.resolve(colors))
}

val BrowserToolbarStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(0.6.cssRem)
        .padding(topBottom = 0.6.cssRem, leftRight = 0.875.cssRem)
        .borderBottom(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

val BrowserDotStyle = CssStyle.base {
    Modifier
        .size(0.6.cssRem)
        .borderRadius(50.percent)
        .backgroundColor(ColorToken.Divider.resolve(colorMode.webColors()))
}

val BrowserAddressStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .height(1.5.cssRem)
        .borderRadius(0.5.cssRem)
        .backgroundColor(ColorToken.Background.resolve(colorMode.webColors()))
}

val BrowserViewportStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(20.cssRem)
        .backgroundColor(ColorToken.Background.resolve(colorMode.webColors()))
}

val PanelPlaceholderStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .minHeight(16.cssRem)
        .borderRadius(1.25.cssRem)
        .border(1.px, LineStyle.Dashed, ColorToken.Divider.resolve(colors))
        .backgroundColor(ColorToken.Background.resolve(colors))
}

@Composable
fun MediaPlaceholder(label: StringDesc, frame: MediaFrame, modifier: Modifier = Modifier) {
    when (frame) {
        MediaFrame.PHONE -> PhonePlaceholder(label, modifier)
        MediaFrame.BROWSER -> BrowserPlaceholder(label, modifier)
        MediaFrame.PANEL -> PanelPlaceholder(label, modifier)
    }
}

@Composable
private fun PhoneFrame(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(
        PhoneFrameStyle
            .toModifier()
            .then(modifier),
    ) {
        Column(
            PhoneScreenStyle
                .toModifier()
                .then(plainSurfaceColors()),
        ) {
            content()
        }
    }
}

@Composable
private fun BrowserFrame(
    address: StringDesc?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        BrowserFrameStyle
            .toModifier()
            .then(modifier),
    ) {
        Row(BrowserToolbarStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.gap(0.375.cssRem)) {
                repeat(BROWSER_DOT_COUNT) { Box(BrowserDotStyle.toModifier()) }
            }
            Box(BrowserAddressStyle.toModifier(), contentAlignment = Alignment.Center) {
                address?.let { BrowserAddress(it) }
            }
        }
        Column(
            BrowserViewportStyle
                .toModifier()
                .then(plainSurfaceColors()),
        ) {
            content()
        }
    }
}

@Composable
private fun BrowserAddress(address: StringDesc) {
    TextBlock(
        address,
        LabelSmallTextStyle
            .toModifier()
            .secondary(),
    )
}

@Composable
private fun plainSurfaceColors(): Modifier {
    return Modifier.surfaceColors(BandTone.PLAIN.surfaces(), ColorMode.current.webColors())
}

@Composable
private fun PhonePlaceholder(label: StringDesc, modifier: Modifier) {
    PhoneFrame(modifier) {
        PlaceholderLabel(label, Modifier.flexGrow(1))
    }
}

@Composable
private fun BrowserPlaceholder(label: StringDesc, modifier: Modifier) {
    BrowserFrame(null, modifier) {
        PlaceholderLabel(label, Modifier.flexGrow(1))
    }
}

@Composable
private fun PanelPlaceholder(label: StringDesc, modifier: Modifier) {
    PlaceholderLabel(
        label,
        PanelPlaceholderStyle
            .toModifier()
            .then(modifier),
    )
}

@Composable
private fun PlaceholderLabel(label: StringDesc, modifier: Modifier) {
    Box(
        Modifier
            .fillMaxWidth()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        TextBlock(
            label,
            LabelMediumTextStyle
                .toModifier()
                .textAlign(TextAlign.Center)
                .secondary(),
        )
    }
}

private const val BROWSER_DOT_COUNT = 3
