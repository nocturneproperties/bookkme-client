package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import library.validation.api.ValidateEmail
import library.validation.api.ValidateEmail.Result.Invalid.Format.isValid
import library.validation.api.ValidateName
import library.validation.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification.Message
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.designsystem.uistate.validate
import kotlinx.coroutines.flow.filterNotNull
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.ObserveSignUpRejection
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpRejection
import me.bookk.feature.authorization.presentation.AuthConstants
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

class SignUpViewModel(
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val startSignUp: StartSignUp,
    private val observeSignUpRejection: ObserveSignUpRejection,
    private val getSignUpLegalDocuments: GetSignUpLegalDocuments,
    private val deviceFacade: DeviceFacade,
    private val stateFactory: AuthStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs),
    SignUpEventListener {

    val uiState: SignUpState = stateFactory.createSignUpState().setup()
    private var isObservingRejection = false

    init {
        loadLegalDocuments()
    }

    private fun SignUpState.setup() = apply {
        setupAppBar()
        setupProfileFields()
        setupLegalAcceptance()
        setupConfirmButton()
        setupPasskeyInfo()
    }

    private fun SignUpState.setupAppBar() {
        appBar.apply {
            title = AuthRes.strings.sign_up_create_acc.desc()
            size = TopBarSize.LARGE
            onBackClick = weakVMClosure {
                it.uiState.navigation.push(SignUpNavigationDestination.Back)
            }
        }
    }

    private fun SignUpState.setupProfileFields() {
        name.apply {
            label = AuthRes.strings.sign_up_first_name.desc()
            placeholder = label
            onTextChanged = weakVMClosure { vm, text ->
                vm.onFirstNameTextChanged(text)
            }
            clearError()
        }
        lastName.apply {
            label = AuthRes.strings.sign_up_last_name.desc()
            placeholder = label
            onTextChanged = weakVMClosure { vm, text ->
                vm.onLastNameTextChanged(text)
            }
            clearError()
        }
        email.apply {
            label = AuthRes.strings.sign_up_email.desc()
            placeholder = label
            onTextChanged = weakVMClosure { vm, text ->
                vm.onEmailTextChanged(text)
            }
            inputType = InputType.EMAIL
            clearError()
        }
    }

    private fun SignUpState.setupLegalAcceptance() {
        legalAcceptance.apply {
            text = AuthRes.strings.sign_up_accept_legal_documents.desc()
            clearError()
            onCheckedChange = weakVMClosure { vm, checked ->
                vm.onLegalAcceptanceChanged(checked)
            }
        }
        reloadLegalDocuments.apply {
            text = AuthRes.strings.sign_up_legal_retry.desc()
            isVisible = false
            onClick = weakVMClosure { it.loadLegalDocuments() }
        }
    }

    private fun SignUpState.setupConfirmButton() {
        confirmButton.apply {
            text = AuthRes.strings.sign_up_continue_button.desc()
            isEnabled = false
            onClick = weakVMClosure { it.onConfirmButtonClick() }
        }
    }

    private fun SignUpState.setupPasskeyInfo() {
        learnMoreButton.apply {
            text = AuthRes.strings.sign_in_passkey_learn_more_button.desc()
            onClick = weakVMClosure { it.onLearnMoreClick() }
        }
        passkeyInfoCardData = PasskeyInfoCardData(
            title = AuthRes.strings.sign_up_why_no_password_title.desc(),
            description = AuthRes.strings.sign_up_why_no_password_description.desc(),
        )
    }

    private fun loadLegalDocuments() {
        launch(
            key = LOAD_LEGAL_DOCUMENTS_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.reloadLegalDocuments.startLoading()
                validateButton()
            },
            call = { getSignUpLegalDocuments() },
            onComplete = {
                uiState.legalDocuments = SignUpLegalDocumentsState(
                    privacyPolicy = createLegalDocumentState(it.privacyPolicy),
                    termsOfService = createLegalDocumentState(it.termsOfService),
                )
                uiState.legalAcceptance.isChecked = false
                uiState.legalAcceptance.clearError()
                uiState.reloadLegalDocuments.isVisible = false
            },
            onError = {
                uiState.legalAcceptance.showError(AuthRes.strings.sign_up_legal_load_error.desc())
                uiState.reloadLegalDocuments.isVisible = true
                uiState.notification.add(it.notification())
            },
            onTerminate = {
                uiState.reloadLegalDocuments.stopLoading()
                validateButton()
            },
        )
    }

    private fun createLegalDocumentState(document: SignUpLegalDocument): SignUpLegalDocumentState {
        return stateFactory.createSignUpLegalDocumentState().apply {
            this.document = document
            linkText = when (document.document) {
                Document.PRIVACY_POLICY -> AuthRes.strings.sign_up_privacy_policy_link.desc()
                Document.TERMS_OF_SERVICE -> AuthRes.strings.sign_up_terms_of_service_link.desc()
            }
            linkUrl = document.url
            onLinkClick = weakVMClosure {
                it.deviceFacade.openUrlPreview(document.url)
            }
        }
    }

    private fun onLegalAcceptanceChanged(checked: Boolean) {
        uiState.legalAcceptance.isChecked = checked
        uiState.legalAcceptance.clearError()
        validateButton()
    }

    private fun acceptedDocuments(): AcceptedLegalDocuments {
        val documents = uiState.legalDocuments
        return AcceptedLegalDocuments(
            privacyPolicy = documents?.privacyPolicy?.document?.version,
            termsOfService = documents?.termsOfService?.document?.version,
        )
    }

    private fun onLegalDocumentsRejected() {
        uiState.notification.add(
            Message.simple(AuthRes.strings.sign_up_legal_documents_changed.desc()),
        )
        loadLegalDocuments()
    }

    override fun onLearnMoreClick() {
        deviceFacade.openUrlPreview(AuthConstants.PASSKEY_INFO_URL)
    }

    override fun onFirstNameTextChanged(text: String) {
        uiState.name.text = text
        uiState.name.clearError()
        validateButton()
    }

    override fun onLastNameTextChanged(text: String) {
        uiState.lastName.text = text
        uiState.lastName.clearError()
        validateButton()
    }

    override fun onEmailTextChanged(text: String) {
        uiState.email.text = text
        uiState.email.clearError()
        validateButton()
    }

    private fun validateForm(): Boolean {
        return listOf(
            uiState.name.validate(
                AuthRes.strings.sign_up_first_name_error.desc().takeIf {
                    !validateName(uiState.name.text).isValid
                },
            ),
            uiState.lastName.validate(
                AuthRes.strings.sign_up_last_name_error.desc().takeIf {
                    !validateName(uiState.lastName.text).isValid
                },
            ),
            uiState.email.validate(
                AuthRes.strings.sign_up_email_error.desc().takeIf {
                    !validateEmail(uiState.email.text).isValid
                },
            ),
            uiState.legalAcceptance.validate(
                AuthRes.strings.sign_up_legal_acceptance_error.desc().takeIf {
                    !uiState.legalAcceptance.isChecked
                },
            ),
        )
            .all { it }
    }

    override fun onConfirmButtonClick() {
        if (!validateForm()) {
            validateButton()
            return
        }
        val userData = StartSignUp.UserData(
            uiState.name.text,
            uiState.lastName.text,
            uiState.email.text,
            acceptedDocuments(),
        )
        launch(
            key = START_SIGN_UP_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.confirmButton.startLoading() },
            call = { startSignUp(userData) },
            onComplete = ::onSignUpStarted,
            onError = ::onStartSignUpError,
            onTerminate = {
                uiState.confirmButton.isLoading = false
                validateButton()
            },
        )
    }

    private fun onSignUpStarted(pendingSignUp: StartSignUp.PendingSignUp) {
        observeRejection()
        uiState.navigation.push(SignUpNavigationDestination.VerifyEmail(pendingSignUp))
    }

    private fun onStartSignUpError(error: Throwable) {
        when (error) {
            is StartSignUp.Error.EmailAlreadyExist -> showEmailError(AuthRes.strings.sign_up_email_exist)
            is StartSignUp.Error.InvalidEmailFormat -> showEmailError(AuthRes.strings.sign_up_email_error)
            is EmailVerificationError.Undeliverable -> {
                showEmailError(AuthRes.strings.sign_up_email_undeliverable)
            }

            is EmailVerificationError.ResendTooSoon -> uiState.notification.add(
                Message.simple(AuthRes.strings.verify_email_resend_too_soon.desc()),
            )

            else -> uiState.notification.add(error.notification())
        }
    }

    private fun observeRejection() {
        if (isObservingRejection) return
        isObservingRejection = true
        observeSignUpRejection()
            .filterNotNull()
            .safeOnEach(::renderRejection)
            .observe()
    }

    private fun renderRejection(rejection: SignUpRejection) {
        when (rejection) {
            SignUpRejection.LEGAL_DOCUMENTS_CHANGED -> onLegalDocumentsRejected()
            SignUpRejection.EMAIL_TAKEN -> showEmailError(AuthRes.strings.sign_up_email_exist)
            SignUpRejection.EMAIL_INVALID -> showEmailError(AuthRes.strings.sign_up_email_error)
            SignUpRejection.EMAIL_UNDELIVERABLE -> {
                showEmailError(AuthRes.strings.sign_up_email_undeliverable)
            }
        }
        validateButton()
    }

    private fun showEmailError(message: StringResource) {
        uiState.email.showError(message.desc())
    }

    private fun validateButton() {
        uiState.confirmButton.isEnabled =
            !uiState.confirmButton.isLoading && !uiState.reloadLegalDocuments.isLoading &&
            !uiState.reloadLegalDocuments.isVisible &&
            uiState.legalDocuments != null && uiState.legalAcceptance.isValid &&
            uiState.name.isValid && uiState.lastName.isValid && uiState.email.isValid
    }

    private companion object {
        const val LOAD_LEGAL_DOCUMENTS_KEY = "load_legal_documents"
        const val START_SIGN_UP_KEY = "start_sign_up"
    }
}
