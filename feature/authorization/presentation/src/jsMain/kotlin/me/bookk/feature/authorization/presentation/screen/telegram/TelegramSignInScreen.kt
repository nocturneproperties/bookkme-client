package me.bookk.feature.authorization.presentation.screen.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import library.telegram.TelegramWebApp
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import me.bookk.site.designsystem.components.ActionButton
import me.bookk.site.designsystem.components.CheckBox
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform

@Composable
fun TelegramSignInScreen(webApp: TelegramWebApp, signedInContent: @Composable () -> Unit) {
    val state = rememberTelegramSignInViewModel(webApp.initData).uiState
    when (state.phase) {
        TelegramSignInPhase.SIGNING_IN -> MessageText(state.message)
        TelegramSignInPhase.LEGAL_ACCEPTANCE -> LegalAcceptance(state, webApp)
        TelegramSignInPhase.SIGNED_IN -> SignedIn(state, signedInContent)
        TelegramSignInPhase.FAILED -> Failure(state)
    }
}

@Composable
private fun rememberTelegramSignInViewModel(initData: String): TelegramSignInViewModel {
    val viewModel = remember(initData) {
        KoinPlatform.getKoin().get<TelegramSignInViewModel> { parametersOf(initData) }
    }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    return viewModel
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
private fun SignedIn(state: TelegramSignInState, signedInContent: @Composable () -> Unit) {
    Heading(state.message, HeadingLevel.H1, HeadlineLargeTextStyle.toModifier())
    signedInContent()
}

@Composable
private fun Failure(state: TelegramSignInState) {
    MessageText(state.message)
    ActionButton(state.retryButton)
}

@Composable
private fun LegalAcceptance(state: TelegramSignInState, webApp: TelegramWebApp) {
    MessageText(state.message)
    state.legalDocuments?.let { LegalDocumentLinks(it, webApp) }
    CheckBox(state.legalAcceptance)
    ActionButton(state.continueButton)
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
