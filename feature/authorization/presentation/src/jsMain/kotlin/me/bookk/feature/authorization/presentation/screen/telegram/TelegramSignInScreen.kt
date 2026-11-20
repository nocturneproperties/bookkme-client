package me.bookk.feature.authorization.presentation.screen.telegram

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import me.bookk.site.designsystem.components.CheckBox
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.koin.core.parameter.parametersOf

@Composable
fun TelegramSignInScreen(webApp: TelegramWebApp) {
    val state = rememberViewModel<TelegramSignInViewModel>(webApp.initData) {
        parametersOf(webApp.initData)
    }.uiState
    when (state.phase) {
        TelegramSignInPhase.SIGNING_IN -> MessageText(state.message)
        TelegramSignInPhase.LEGAL_ACCEPTANCE -> LegalAcceptance(state, webApp)
        TelegramSignInPhase.FAILED -> Failure(state, webApp)
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
    MessageText(state.message)
    state.legalDocuments?.let { LegalDocumentLinks(it, webApp) }
    CheckBox(state.legalAcceptance)
    TelegramMainButton(webApp, state.continueButton)
}

@Composable
private fun LegalDocumentLinks(documents: SignUpLegalDocumentsState, webApp: TelegramWebApp) {
    Row(Modifier.gap(1.5.cssRem)) {
        LegalDocumentLink(documents.privacyPolicy, webApp)
        LegalDocumentLink(documents.termsOfService, webApp)
    }
}

@Composable
private fun LegalDocumentLink(document: SignUpLegalDocumentState, webApp: TelegramWebApp) {
    TextLink(
        document.linkText,
        document.linkUrl,
        BodyMediumTextStyle
            .toModifier()
            .active()
            .onClick { event ->
                event.preventDefault()
                webApp.openLink(document.linkUrl)
            },
    )
}
