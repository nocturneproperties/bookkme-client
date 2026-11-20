package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.simple.ErrorState
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.error
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.uistate.WebButtonState
import org.jetbrains.compose.web.css.cssRem

@Composable
fun EmptyStateView(state: EmptyState) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(2.cssRem)
            .gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(state.image.fileUrl, modifier = Modifier.height(8.cssRem))
        TextBlock(
            state.label,
            BodyMediumTextStyle
                .toModifier()
                .textAlign(TextAlign.Center),
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
            BodyMediumTextStyle
                .toModifier()
                .textAlign(TextAlign.Center)
                .secondary(),
        )
        ActionButton(retry)
    }
}

@Composable
fun BannerErrorView(state: BannerErrorState) {
    val retry = remember(state) {
        WebButtonState(
            text = DesignSystem.strings.action_retry.desc(),
            isLoading = state.isRetrying,
            onClick = state.onRetryClick,
        )
    }
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextBlock(
                state.text,
                BodyMediumTextStyle
                    .toModifier()
                    .error(),
            )
            Spacer()
            TextButton(retry)
        }
    }
}
