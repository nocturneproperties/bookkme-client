package me.bookk.feature.authorization.presentation.screen.telegram

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.CheckBox
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.HorizontalDivider
import me.bookk.site.designsystem.components.ListRowButton
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TintedIcon
import me.bookk.site.designsystem.resources.WebDesignSystem
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.koin.core.parameter.parametersOf

@Composable
fun TelegramSignInScreen(
    webApp: TelegramWebApp,
    messageLayout: @Composable (content: @Composable () -> Unit) -> Unit,
    formLayout: @Composable (content: @Composable () -> Unit) -> Unit,
) {
    val state = rememberViewModel<TelegramSignInViewModel>(webApp.initData) {
        parametersOf(webApp.initData)
    }.uiState
    when (state.phase) {
        TelegramSignInPhase.SIGNING_IN -> messageLayout { MessageText(state.message) }
        TelegramSignInPhase.LEGAL_ACCEPTANCE -> formLayout { LegalAcceptance(state, webApp) }
        TelegramSignInPhase.FAILED -> messageLayout { Failure(state, webApp) }
    }
}

@Composable
private fun MessageText(text: StringDesc) {
    TextBlock(
        text,
        BodyLargeTextStyle
            .toModifier()
            .secondary(),
    )
}

@Composable
private fun Failure(state: TelegramSignInState, webApp: TelegramWebApp) {
    MessageText(state.message)
    TelegramMainButton(webApp, state.retryButton)
}

@Composable
private fun LegalAcceptance(state: TelegramSignInState, webApp: TelegramWebApp) {
    Welcome(state.message)
    state.legalDocuments?.let { LegalDocuments(it, state.legalAcceptance, webApp) }
    TelegramMainButton(webApp, state.continueButton)
}

@Composable
private fun Welcome(message: StringDesc) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem),
    ) {
        Heading(
            AuthRes.strings.telegram_sign_in_welcome_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
        TextBlock(
            message,
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px)
                .secondary(),
        )
    }
}

@Composable
private fun LegalDocuments(
    documents: SignUpLegalDocumentsState,
    acceptance: BooleanState,
    webApp: TelegramWebApp,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem),
    ) {
        Heading(
            AuthRes.strings.telegram_sign_in_legal_section.desc(),
            HeadingLevel.H2,
            TitleMediumTextStyle
                .toModifier()
                .margin(0.px),
        )
        AppCard(
            Modifier
                .padding(0.px)
                .gap(0.px),
        ) {
            LegalDocumentRow(
                documents.privacyPolicy,
                WebDesignSystem.images.material_policy,
                webApp,
            )
            HorizontalDivider()
            LegalDocumentRow(
                documents.termsOfService,
                WebDesignSystem.images.material_description,
                webApp,
            )
            HorizontalDivider()
            CheckBox(acceptance, Modifier.padding(leftRight = 1.cssRem, topBottom = 0.75.cssRem))
        }
    }
}

@Composable
private fun LegalDocumentRow(
    document: SignUpLegalDocumentState,
    icon: ImageResource,
    webApp: TelegramWebApp,
) {
    ListRowButton(
        onClick = { webApp.openLink(document.linkUrl) },
        modifier = Modifier.minHeight(3.25.cssRem),
        endIcon = WebDesignSystem.images.material_open_in_new,
    ) {
        TintedIcon(icon, ColorToken.SecondaryText)
        TextBlock(
            document.linkText,
            BodyLargeTextStyle
                .toModifier()
                .fillMaxWidth()
                .margin(0.px),
        )
    }
}
