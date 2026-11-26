package me.bookk.feature.settings.presentation.confirmaction

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramButtonStyle
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.IconTile
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.error
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val ConfirmActionModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.25.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

@Composable
internal fun ConfirmActionScreen(state: ConfirmActionState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.mainButton, state.buttonStyle())
    Column(ConfirmActionModifier) {
        ActionCard(state)
        state.error?.let { ErrorText(it) }
    }
}

private fun ConfirmActionState.buttonStyle(): TelegramButtonStyle {
    return if (isDestructive) TelegramButtonStyle.DESTRUCTIVE else TelegramButtonStyle.PRIMARY
}

@Composable
private fun ActionCard(state: ConfirmActionState) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 1.375.cssRem, leftRight = 1.25.cssRem)
            .gap(0.75.cssRem),
    ) {
        state.icon?.let { IconTile(it, color = state.iconColor) }
        TextBlock(
            state.message,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
    }
}

@Composable
private fun ErrorText(error: StringDesc) {
    TextBlock(
        error,
        BodyMediumTextStyle
            .toModifier()
            .margin(0.px)
            .padding(leftRight = 1.cssRem)
            .error(),
    )
}
