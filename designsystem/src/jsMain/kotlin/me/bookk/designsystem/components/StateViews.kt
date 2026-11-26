package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
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
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.LabelMediumTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.theme.webColors
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.simple.ErrorState
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val BANNER_TINT_ALPHA = 0.12f
private const val BANNER_RETRY_HOVER_OPACITY = 0.75

val BannerErrorStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .gap(0.5.cssRem)
        .padding(topBottom = 0.75.cssRem, leftRight = 1.cssRem)
        .color(colors.error)
        .backgroundColor(
            ColorToken.Error
                .resolve(colors)
                .toRgb()
                .copyf(alpha = BANNER_TINT_ALPHA),
        )
}

val BannerErrorTextStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .minWidth(0.px)
        .whiteSpace(WhiteSpace.NoWrap)
        .overflow(Overflow.Hidden)
        .textOverflow(TextOverflow.Ellipsis)
}

val BannerRetryButtonStyle = CssStyle {
    base {
        Modifier
            .flexShrink(0)
            .padding(0.px)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .backgroundColor(Colors.Transparent)
            .color(colorMode.webColors().error)
            .fontWeight(FontWeight.SemiBold)
            .cursor(Cursor.Pointer)
    }
    cssRule(":hover") { Modifier.opacity(BANNER_RETRY_HOVER_OPACITY) }
}

@Composable
fun EmptyStateView(state: EmptyState) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(2.cssRem)
            .gap(1.5.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TintedIcon(
            DesignSystem.images.material_inbox,
            ColorToken.SecondaryText,
            Modifier.size(3.cssRem),
        )
        TextBlock(
            state.label,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px)
                .textAlign(TextAlign.Center)
                .secondary(),
        )
        state.refreshAction?.let { refresh ->
            val retry =
                remember(state) {
                    WebButtonState(DesignSystem.strings.action_retry.desc(), onClick = refresh)
                }
            TextButton(retry)
        }
    }
}

@Composable
fun ErrorStateView(state: ErrorState) {
    val retry =
        remember(state) {
            WebButtonState(DesignSystem.strings.action_retry.desc(), onClick = state.onRetryClick)
        }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(2.cssRem)
            .gap(0.75.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextBlock(
            state.title,
            TitleMediumTextStyle
                .toModifier()
                .textAlign(TextAlign.Center),
        )
        TextBlock(
            state.subtitle,
            BodyLargeTextStyle
                .toModifier()
                .textAlign(TextAlign.Center)
                .secondary(),
        )
        ActionButton(retry)
    }
}

@Composable
fun BannerErrorView(state: BannerErrorState, modifier: Modifier = Modifier) {
    Row(
        BannerErrorStyle
            .toModifier()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TintedIcon(
            DesignSystem.images.material_warning,
            ColorToken.Error,
            Modifier.size(1.125.cssRem),
        )
        Span(
            LabelMediumTextStyle
                .toModifier()
                .then(BannerErrorTextStyle.toModifier())
                .toAttrs(),
        ) { Text(state.text.localized()) }
        BannerRetry(state)
    }
}

@Composable
private fun BannerRetry(state: BannerErrorState) {
    if (state.isRetrying) {
        Spinner(
            Modifier
                .size(1.cssRem)
                .borderTop(2.px, LineStyle.Solid, ColorToken.Error.themed),
        )
        return
    }
    Button(
        LabelMediumTextStyle
            .toModifier()
            .then(BannerRetryButtonStyle.toModifier())
            .toAttrs { onClick { state.onRetryClick() } },
    ) { Text(DesignSystem.strings.action_retry.desc().localized()) }
}
