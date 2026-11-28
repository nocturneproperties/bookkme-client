package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.LabelMediumTextStyle
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.theme.webColors
import me.bookk.designsystem.uistate.ButtonState
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val ALERT_STRIP_HEIGHT_REM = 2.75
private const val ALERT_STRIP_TINT_ALPHA = 0.12f
private const val ALERT_STRIP_LINE_ALPHA = 0.2f
private const val ALERT_STRIP_ACTION_HOVER_OPACITY = 0.75
private const val BOTTOM_INSET_PROPERTY = "--bookkme-bottom-inset"

private const val TELEGRAM_BOTTOM_INSET =
    "var(--tg-safe-area-inset-bottom, 0px) + var(--tg-content-safe-area-inset-bottom, 0px)"
private const val SAFE_BOTTOM_INSET =
    "max($TELEGRAM_BOTTOM_INSET, env(safe-area-inset-bottom, 0px))"
private const val ALERT_STRIP_HEIGHT = "calc(${ALERT_STRIP_HEIGHT_REM}rem + $SAFE_BOTTOM_INSET)"

internal const val BOTTOM_INSET = "var($BOTTOM_INSET_PROPERTY, 0px)"

val AlertStripStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .position(Position.Sticky)
        .bottom(0.px)
        .zIndex(2)
        .gap(0.5.cssRem)
        .padding(left = 1.cssRem, right = 0.5.cssRem)
        .styleModifier {
            property("height", ALERT_STRIP_HEIGHT)
            property("margin-top", "calc(-1 * $ALERT_STRIP_HEIGHT)")
            property("padding-bottom", SAFE_BOTTOM_INSET)
        }
        .color(colors.error)
        .backgroundColor(colors.error.over(colors.background, ALERT_STRIP_TINT_ALPHA))
        .borderTop(
            1.px,
            LineStyle.Solid,
            colors.error.toRgb().copyf(alpha = ALERT_STRIP_LINE_ALPHA),
        )
}

val AlertStripTextStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .minWidth(0.px)
        .margin(0.px)
        .whiteSpace(WhiteSpace.NoWrap)
        .overflow(Overflow.Hidden)
        .textOverflow(TextOverflow.Ellipsis)
}

val AlertStripActionStyle = CssStyle {
    base {
        Modifier
            .flexShrink(0)
            .fillMaxHeight()
            .padding(leftRight = 0.5.cssRem)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .backgroundColor(Colors.Transparent)
            .color(colorMode.webColors().error)
            .fontWeight(FontWeight.Bold)
            .cursor(Cursor.Pointer)
    }
    cssRule(":hover") { Modifier.opacity(ALERT_STRIP_ACTION_HOVER_OPACITY) }
}

@Composable
fun BottomStripLayout(
    isStripShown: Boolean,
    strip: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val inset = if (isStripShown) ALERT_STRIP_HEIGHT else "0px"
    Div(
        Modifier
            .fillMaxWidth()
            .styleModifier { property(BOTTOM_INSET_PROPERTY, inset) }
            .toAttrs(),
    ) {
        content()
        if (isStripShown) strip()
    }
}

@Composable
fun AlertStrip(text: StringDesc, action: ButtonState, icon: ImageResource) {
    Row(
        AlertStripStyle.toModifier(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TintedIcon(icon, ColorToken.Error, Modifier.size(1.cssRem))
        Span(
            LabelMediumTextStyle
                .toModifier()
                .then(AlertStripTextStyle.toModifier())
                .toAttrs(),
        ) { Text(text.localized()) }
        AlertStripAction(action)
    }
}

@Composable
private fun AlertStripAction(action: ButtonState) {
    if (action.isLoading) {
        Spinner(
            Modifier
                .margin(leftRight = 0.875.cssRem)
                .size(1.cssRem)
                .borderTop(2.px, LineStyle.Solid, ColorToken.Error.themed),
        )
    } else {
        Button(
            LabelMediumTextStyle
                .toModifier()
                .then(AlertStripActionStyle.toModifier())
                .toAttrs {
                    if (!action.isEnabled) disabled()
                    onClick { action.onClick?.invoke() }
                },
        ) { Text(action.text.localized()) }
    }
}

private fun Color.over(background: Color, alpha: Float): Color {
    val top = toRgb()
    val bottom = background.toRgb()
    return Color.rgb(
        r = top.red.blendedWith(bottom.red, alpha),
        g = top.green.blendedWith(bottom.green, alpha),
        b = top.blue.blendedWith(bottom.blue, alpha),
    )
}

private fun Int.blendedWith(bottom: Int, alpha: Float): Int {
    return (this * alpha + bottom * (1 - alpha)).toInt()
}
