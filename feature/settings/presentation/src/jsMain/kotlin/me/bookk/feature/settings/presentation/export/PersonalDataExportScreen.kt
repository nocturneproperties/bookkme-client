package me.bookk.feature.settings.presentation.export

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramBackButton
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.HorizontalDivider
import me.bookk.designsystem.components.IconTile
import me.bookk.designsystem.components.ProgressTile
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TintedIcon
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.error
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.feature.settings.presentation.confirmation.AskTelegramWriteAccess
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationNotice
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val PersonalDataExportModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

@Composable
internal fun PersonalDataExportScreen(state: PersonalDataExportState, webApp: TelegramWebApp) {
    TelegramBackButton(webApp, state.appBar.onBackClick)
    TelegramMainButton(webApp, mainAction(state))
    AskTelegramWriteAccess(webApp, state.chatConfirmation)
    Column(PersonalDataExportModifier) {
        Heading(
            SettingsRes.strings.settings_export_headline.desc(),
            HeadingLevel.H1,
            textModifier(HeadlineLargeTextStyle).fontWeight(FontWeight.ExtraBold),
        )
        StatusCard(state.status, state.expiry)
        ChatConfirmationNotice(state.chatConfirmation)
        Notes(state)
    }
}

private fun mainAction(state: PersonalDataExportState): ButtonState {
    return if (state.download.isVisible) state.download else state.request
}

@Composable
private fun StatusCard(status: UIExportStatus, expiry: StringDesc?) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .gap(0.75.cssRem),
    ) {
        StatusIndicator(status)
        Column(Modifier.fillMaxWidth().gap(0.25.cssRem)) {
            TextBlock(
                status.title,
                textModifier(HeadlineSmallTextStyle).fontWeight(FontWeight.SemiBold),
            )
            status.message?.let { TextBlock(it, textModifier(BodyLargeTextStyle).secondary()) }
        }
        if (status.showsFile) {
            FileRow()
        }
        expiry?.let { ExpiryRow(it) }
    }
}

@Composable
private fun StatusIndicator(status: UIExportStatus) {
    if (status.showsFile) {
        TintedIcon(
            DesignSystem.images.material_check_circle,
            status.color,
            Modifier.size(2.5.cssRem),
        )
    } else {
        StatusTile(status)
    }
}

@Composable
private fun StatusTile(status: UIExportStatus) {
    if (status.isInProgress) {
        ProgressTile(color = status.color)
    } else {
        IconTile(status.stage.icon(), color = status.color)
    }
}

private fun UIExportStage.icon(): ImageResource {
    return when (this) {
        UIExportStage.REQUEST,
        UIExportStage.PREPARING,
        -> DesignSystem.images.material_download

        UIExportStage.READY -> DesignSystem.images.material_check

        UIExportStage.FAILED -> DesignSystem.images.material_warning

        UIExportStage.EXPIRED -> DesignSystem.images.material_schedule
    }
}

@Composable
private fun FileRow() {
    Row(
        Modifier.fillMaxWidth().gap(1.cssRem),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TintedIcon(
            DesignSystem.images.material_description,
            ColorToken.SecondaryText,
            Modifier.size(2.cssRem),
        )
        Column(Modifier.minWidth(0.px)) {
            TextBlock(
                SettingsRes.strings.settings_export_file_title.desc(),
                textModifier(BodyLargeTextStyle).fontWeight(FontWeight.SemiBold),
            )
            TextBlock(
                SettingsRes.strings.settings_export_file_type.desc(),
                textModifier(BodyLargeTextStyle).secondary(),
            )
        }
    }
}

@Composable
private fun ExpiryRow(expiry: StringDesc) {
    HorizontalDivider()
    Row(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextBlock(
            SettingsRes.strings.settings_export_expiry_label.desc(),
            textModifier(BodyLargeTextStyle),
        )
        TextBlock(expiry, textModifier(BodyLargeTextStyle).textAlign(TextAlign.End))
    }
}

@Composable
private fun Notes(state: PersonalDataExportState) {
    Column(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(leftRight = 1.cssRem)
            .gap(0.5.cssRem),
    ) {
        state.requestAvailability?.let {
            TextBlock(it, textModifier(BodyMediumTextStyle).secondary())
        }
        state.error?.let { TextBlock(it, textModifier(BodyLargeTextStyle).error()) }
        TextButton(state.retry)
        state.status.footer?.let {
            TextBlock(
                it,
                textModifier(BodyMediumTextStyle)
                    .secondary()
                    .fillMaxWidth()
                    .textAlign(TextAlign.Center),
            )
        }
    }
}

@Composable
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
