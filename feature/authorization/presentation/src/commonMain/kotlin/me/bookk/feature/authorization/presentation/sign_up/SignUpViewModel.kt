package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import library.validation.api.ValidateEmail
import library.validation.api.ValidateEmail.Result.Invalid.Format.isValid
import library.validation.api.ValidateName
import library.validation.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.Message
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.validate
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.Error
import me.bookk.feature.authorization.presentation.AuthConstants
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

class SignUpViewModel(
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val createAccount: CreateAccount,
    private val deviceFacade: DeviceFacade,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignUpEventListener {

    val uiState: SignUpState = stateFactory.createSignUpState(createInitData()).apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(SignUpNavigationDestination.Back) }
        name.clearError()
        lastName.clearError()
        email.clearError()
        email.inputType = InputType.EMAIL
        confirmButton.isEnabled = true
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
                AuthRes.strings.sign_up_first_name_error.desc().takeIf { !validateName(uiState.name.text).isValid }
            ),
            uiState.lastName.validate(
                AuthRes.strings.sign_up_last_name_error.desc().takeIf { !validateName(uiState.lastName.text).isValid }
            ),
            uiState.email.validate(
                AuthRes.strings.sign_up_email_error.desc().takeIf { !validateEmail(uiState.email.text).isValid }
            )
        ).all { it }
    }

    override fun onConfirmButtonClick() {
        if (!validateForm()) {
            validateButton()
            return
        }
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.confirmButton.isLoading = true },
            call = {
                createAccount(
                    CreateAccount.UserData(
                        uiState.name.text,
                        uiState.lastName.text,
                        uiState.email.text
                    )
                )
            },
            onComplete = { uiState.notification.add(PresentationNotification.SuccessHaptic) },
            onError = {
                when (it) {
                    is Error.EmailAlreadyExist ->
                        uiState.email.showError(AuthRes.strings.sign_up_email_exist.desc())
                    is Error.InvalidEmailFormat ->
                        uiState.email.showError(AuthRes.strings.sign_up_email_error.desc())
                    is Error.PasskeyVerificationFailed -> {
                        uiState.notification.add(
                            Message(
                                message = AuthRes.strings.sign_up_passkey_failed.desc(),
                                buttons = listOf(
                                    ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()),
                                )
                            )
                        )
                    }
                    is Error.AccountCreationFailed -> {
                        uiState.notification.add(
                            Message(
                                message = AuthRes.strings.sign_up_failed.desc(),
                                buttons = listOf(
                                    ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()),
                                )
                            )
                        )
                    }
                    else -> uiState.notification.add(errorMapper.mapToNotification(it))
                }
            },
            onTerminate = {
                uiState.confirmButton.isLoading = false
                validateButton()
            },
        )
    }

    private fun validateButton() {
        uiState.confirmButton.isEnabled = uiState.name.isValid &&
                uiState.lastName.isValid &&
                uiState.email.isValid
    }

    companion object {
        fun createInitData() = SignUpState.InitData(
            title = AuthRes.strings.sign_up_create_acc.desc(),
            nameHint = AuthRes.strings.sign_up_first_name.desc(),
            lastNameHint = AuthRes.strings.sign_up_last_name.desc(),
            emailHint = AuthRes.strings.sign_up_email.desc(),
            learnMoreButtonText = AuthRes.strings.sign_in_passkey_learn_more_button.desc(),
            passkeyInfoCardData = PasskeyInfoCardData(
                title = AuthRes.strings.sign_up_why_no_password_title.desc(),
                description = AuthRes.strings.sign_up_why_no_password_description.desc()
            ),
            confirmButtonText = AuthRes.strings.sign_up_create_account_button.desc()
        )
    }
}