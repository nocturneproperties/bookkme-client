package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BandTone
import me.bookk.designsystem.theme.colorMode
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.surfaceColors
import me.bookk.designsystem.theme.surfaces
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem

val ContentMaxWidth = 72.cssRem

val SectionContainerStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(ContentMaxWidth)
        .padding(leftRight = 1.cssRem)
}

val BandStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 4.cssRem)
    }
    Breakpoint.MD { Modifier.padding(topBottom = 6.5.cssRem) }
}

val CompactBandStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 2.5.cssRem)
    }
    Breakpoint.MD { Modifier.padding(topBottom = 3.5.cssRem) }
}

@Composable
fun Band(
    modifier: Modifier = Modifier,
    tone: BandTone = BandTone.PLAIN,
    isCompact: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    BandSurface(modifier, tone, isCompact) {
        SectionContainer(content = content)
    }
}

@Composable
fun BandSurface(
    modifier: Modifier = Modifier,
    tone: BandTone = BandTone.PLAIN,
    isCompact: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = if (isCompact) CompactBandStyle else BandStyle
    val pageMode = ColorMode.current
    val bandMode = tone.colorMode(pageMode)
    val colors = bandMode.webColors()
    val surfaces = tone.surfaces()
    Surface(
        spacing
            .toModifier()
            .backgroundColor(surfaces.background.resolve(colors))
            .color(ColorToken.PrimaryText.resolve(colors))
            .surfaceColors(surfaces, colors)
            .then(modifier),
        colorModeOverride = bandMode.takeIf { it != pageMode },
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            content()
        }
    }
}

@Composable
fun SectionContainer(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        SectionContainerStyle
            .toModifier()
            .then(modifier),
    ) {
        content()
    }
}
