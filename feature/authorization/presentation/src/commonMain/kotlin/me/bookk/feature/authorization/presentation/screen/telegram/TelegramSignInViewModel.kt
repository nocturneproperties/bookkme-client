package me.bookk.feature.authorization.presentation.screen.telegram

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.authorization.presentation.ClientStateFactory
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import org.koin.core.annotation.InjectedParam

class TelegramSignInViewModel(
    @InjectedParam private val initData: String,
    private val signInWithTelegram: SignInWithTelegram,
    private val getSignUpLegalDocuments: GetSignUpLegalDocuments,
    private val stateFactory: ClientStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: TelegramSignInState = stateFactory.createTelegramSignInState().setup()

    init {
        signIn()
    }

    private fun TelegramSignInState.setup() = apply {
        legalAcceptance.text = AuthRes.strings.sign_up_accept_legal_documents.desc()
        legalAcceptance.onCheckedChange = weakVMClosure { vm, checked ->
            vm.onLegalAcceptanceChanged(checked)
        }
        continueButton.text = AuthRes.strings.telegram_sign_in_continue.desc()
        continueButton.onClick = weakVMClosure { it.signInWithAcceptedDocuments() }
        retryButton.text = AuthRes.strings.telegram_sign_in_retry.desc()
        retryButton.onClick = weakVMClosure { it.signIn() }
    }

    private fun signIn() {
        showPhase(TelegramSignInPhase.SIGNING_IN, PROGRESS)
        launchSignIn(acceptedDocuments = null)
    }

    private fun signInWithAcceptedDocuments() {
        launchSignIn(acceptedDocuments())
    }

    private fun launchSignIn(acceptedDocuments: AcceptedLegalDocuments?) {
        launch(
            key = SIGN_IN_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.continueButton.startLoading() },
            call = { signInWithTelegram(initData, acceptedDocuments) },
            onError = ::onSignInFailed,
            onTerminate = {
                uiState.continueButton.stopLoading(enable = uiState.legalAcceptance.isChecked)
            },
        )
    }

    private fun onSignInFailed(error: Throwable) {
        when (error) {
            is SignInWithTelegram.Error.LegalDocumentsNotAccepted -> {
                loadLegalDocuments(LEGAL_PROMPT)
            }

            is SignInWithTelegram.Error.LegalDocumentVersionUnknown -> {
                loadLegalDocuments(DOCUMENTS_CHANGED)
            }

            is SignInWithTelegram.Error.TelegramDataExpired -> {
                showFailure(EXPIRED.desc(), false)
            }

            is SignInWithTelegram.Error.TelegramDataInvalid -> {
                showFailure(INVALID.desc(), false)
            }

            is SignInWithTelegram.Error.Unavailable -> {
                showFailure(UNAVAILABLE.desc(), true)
            }

            is SignInWithTelegram.Error.AccountCreationFailed -> {
                showFailure(ACCOUNT_CREATION_FAILED.desc(), true)
            }

            else -> {
                showFailure(error.description().message, true)
            }
        }
    }

    private fun loadLegalDocuments(prompt: StringResource) {
        launch(
            key = LEGAL_DOCUMENTS_KEY,
            launchBehaviour = LaunchBehaviour.DropOldest,
            launchIn = DispatcherProvider.io,
            call = { getSignUpLegalDocuments.refresh() },
            onComplete = { showLegalDocuments(it, prompt) },
            onError = { showFailure(LEGAL_LOAD_ERROR.desc(), true) },
        )
    }

    private fun showLegalDocuments(documents: SignUpLegalDocuments, prompt: StringResource) {
        uiState.legalDocuments = SignUpLegalDocumentsState(
            privacyPolicy = createLegalDocumentState(documents.privacyPolicy),
            termsOfService = createLegalDocumentState(documents.termsOfService),
        )
        onLegalAcceptanceChanged(false)
        showPhase(TelegramSignInPhase.LEGAL_ACCEPTANCE, prompt)
    }

    private fun createLegalDocumentState(document: SignUpLegalDocument): SignUpLegalDocumentState {
        return stateFactory.createLegalDocumentState().apply {
            this.document = document
            linkText = when (document.document) {
                Document.PRIVACY_POLICY -> AuthRes.strings.sign_up_privacy_policy_link.desc()
                Document.TERMS_OF_SERVICE -> AuthRes.strings.sign_up_terms_of_service_link.desc()
            }
            linkUrl = document.url
        }
    }

    private fun onLegalAcceptanceChanged(checked: Boolean) {
        uiState.legalAcceptance.isChecked = checked
        uiState.continueButton.isEnabled = checked
    }

    private fun acceptedDocuments(): AcceptedLegalDocuments {
        val documents = uiState.legalDocuments
        return AcceptedLegalDocuments(
            privacyPolicy = documents?.privacyPolicy?.document?.version,
            termsOfService = documents?.termsOfService?.document?.version,
        )
    }

    private fun showFailure(message: StringDesc, canRetry: Boolean) {
        uiState.retryButton.isVisible = canRetry
        uiState.message = message
        uiState.phase = TelegramSignInPhase.FAILED
    }

    private fun showPhase(phase: TelegramSignInPhase, message: StringResource) {
        uiState.message = message.desc()
        uiState.phase = phase
    }

    private companion object {
        const val SIGN_IN_KEY = "telegram_sign_in"
        const val LEGAL_DOCUMENTS_KEY = "telegram_sign_in_legal_documents"
        val PROGRESS = AuthRes.strings.telegram_sign_in_progress
        val LEGAL_PROMPT = AuthRes.strings.telegram_sign_in_legal_prompt
        val DOCUMENTS_CHANGED = AuthRes.strings.sign_up_legal_documents_changed
        val EXPIRED = AuthRes.strings.telegram_sign_in_expired
        val INVALID = AuthRes.strings.telegram_sign_in_invalid
        val UNAVAILABLE = AuthRes.strings.telegram_sign_in_unavailable
        val ACCOUNT_CREATION_FAILED = AuthRes.strings.telegram_sign_in_account_creation_failed
        val LEGAL_LOAD_ERROR = AuthRes.strings.sign_up_legal_load_error
    }
}
