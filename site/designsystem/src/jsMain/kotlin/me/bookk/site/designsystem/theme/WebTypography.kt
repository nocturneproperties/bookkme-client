package me.bookk.site.designsystem.theme

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import me.bookk.designsystem.resources.color.ColorToken
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val DisplayTextStyle = CssStyle {
    base {
        Modifier
            .fontSize(2.25.cssRem)
            .fontWeight(FontWeight.Bold)
            .lineHeight(1.15)
            .margin(0.px)
    }
    Breakpoint.MD { Modifier.fontSize(3.5.cssRem) }
}

val HeadlineLargeTextStyle = CssStyle {
    base {
        Modifier
            .fontSize(1.75.cssRem)
            .fontWeight(FontWeight.Bold)
            .lineHeight(1.2)
            .margin(0.px)
    }
    Breakpoint.MD { Modifier.fontSize(2.25.cssRem) }
}

val HeadlineSmallTextStyle = CssStyle.base {
    Modifier
        .fontSize(1.5.cssRem)
        .fontWeight(FontWeight.SemiBold)
        .lineHeight(1.25)
        .margin(0.px)
}

val TitleMediumTextStyle = CssStyle.base {
    Modifier
        .fontSize(1.1.cssRem)
        .fontWeight(FontWeight.SemiBold)
        .lineHeight(1.35)
        .margin(0.px)
}

val BodyLargeTextStyle = CssStyle {
    base {
        Modifier
            .fontSize(1.15.cssRem)
            .lineHeight(1.5)
            .margin(0.px)
    }
    Breakpoint.MD { Modifier.fontSize(1.3.cssRem) }
}

val BodyMediumTextStyle = CssStyle.base {
    Modifier
        .fontSize(1.cssRem)
        .lineHeight(1.5)
        .margin(0.px)
}

val BodySmallTextStyle = CssStyle.base {
    Modifier
        .fontSize(0.85.cssRem)
        .lineHeight(1.4)
        .margin(0.px)
}

val LabelMediumTextStyle = CssStyle.base {
    Modifier
        .fontSize(0.8.cssRem)
        .lineHeight(1.4)
        .margin(0.px)
}

val LabelSmallTextStyle = CssStyle.base {
    Modifier
        .fontSize(0.75.cssRem)
        .lineHeight(1.4)
        .margin(0.px)
}

@Composable
fun Modifier.primary(): Modifier {
    return color(ColorToken.PrimaryText.themed)
}

@Composable
fun Modifier.secondary(): Modifier {
    return color(ColorToken.SecondaryText.themed)
}

@Composable
fun Modifier.active(): Modifier {
    return color(ColorToken.ActionText.themed)
}

@Composable
fun Modifier.error(): Modifier {
    return color(ColorToken.Error.themed)
}
