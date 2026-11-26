package me.bookk.feature.settings.presentation.accdelete

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
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramButtonStyle
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.CheckBox
import me.bookk.designsystem.components.IconTile
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.feature.settings.presentation.confirmation.AskTelegramWriteAccess
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationNotice
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val DeleteAccountModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.25.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

@Composable
internal fun DeleteAccountScreen(state: DeleteAccountState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.deleteButton, TelegramButtonStyle.DESTRUCTIVE)
    AskTelegramWriteAccess(webApp, state.chatConfirmation)
    Column(DeleteAccountModifier) {
        WarningCard(state)
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(topBottom = 1.cssRem, leftRight = 1.25.cssRem),
        ) {
            CheckBox(state.confirmation)
        }
        ChatConfirmationNotice(state.chatConfirmation)
    }
}

@Composable
private fun WarningCard(state: DeleteAccountState) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 1.375.cssRem, leftRight = 1.25.cssRem)
            .gap(0.75.cssRem),
    ) {
        IconTile(DesignSystem.images.material_delete, color = ColorToken.Error)
        TextBlock(
            state.confirmationMessage,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
    }
}
