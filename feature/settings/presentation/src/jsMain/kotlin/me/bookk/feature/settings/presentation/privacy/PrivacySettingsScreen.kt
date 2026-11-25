package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.HorizontalDivider
import me.bookk.designsystem.components.InfoNote
import me.bookk.designsystem.components.ListRowButton
import me.bookk.designsystem.components.Spinner
import me.bookk.designsystem.components.StatusLabel
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val PrivacySettingsModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

private val StaticLegalDocumentRowModifier = Modifier
    .fillMaxWidth()
    .boxSizing(BoxSizing.BorderBox)
    .minHeight(4.cssRem)
    .padding(leftRight = 1.cssRem, topBottom = 0.625.cssRem)

@Composable
internal fun PrivacySettingsScreen(state: PrivacySettingsState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.legal.accept)
    Column(PrivacySettingsModifier) {
        state.legal.notice?.let { InfoNote(it) }
        LegalDocuments(state.legal)
    }
}

@Composable
private fun LegalDocuments(legal: LegalDocumentsSection) {
    Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
        LegalHeader(legal)
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(0.px)
                .gap(0.px),
        ) {
            legal.documents.forEachIndexed { index, document ->
                key(document.id) { LegalDocumentRow(document) }
                if (index != legal.documents.lastIndex) LegalDocumentDivider()
            }
        }
        if (legal.retry.isVisible) TextButton(legal.retry)
        legal.message?.let { LegalMessage(it) }
    }
}

@Composable
private fun LegalHeader(legal: LegalDocumentsSection) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(leftRight = 1.cssRem)
            .gap(0.75.cssRem),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Header(legal.title, Modifier.margin(0.px).flexGrow(1))
        if (legal.isLoading) Spinner()
    }
}

@Composable
private fun LegalDocumentRow(document: LegalDocumentItem) {
    val onClick = document.onClick
    if (onClick == null) {
        Row(
            StaticLegalDocumentRowModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegalDocumentText(document)
        }
    } else {
        ListRowButton(
            onClick = onClick,
            endIcon = DesignSystem.images.material_open_in_new,
        ) {
            LegalDocumentText(document)
        }
    }
}

@Composable
private fun LegalDocumentText(document: LegalDocumentItem) {
    Column(
        Modifier
            .flexGrow(1)
            .minWidth(0.px)
            .gap(0.25.cssRem),
    ) {
        TextBlock(
            document.title,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
        StatusLabel(document.status, document.statusColor)
    }
}

@Composable
private fun LegalDocumentDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(left = 1.cssRem),
    ) {
        HorizontalDivider()
    }
}

@Composable
private fun LegalMessage(message: StringDesc) {
    TextBlock(
        message,
        BodySmallTextStyle
            .toModifier()
            .margin(0.px)
            .padding(leftRight = 1.cssRem)
            .secondary(),
    )
}
