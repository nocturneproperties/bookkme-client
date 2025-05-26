package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification.Message
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.Error
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateEmail.Result.Invalid.Format.isValid
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.feature.authorization.presentation.AuthConstants
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.platform.domain.api.OpenUrlPreview

class SignUpViewModel(
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val createAccount: CreateAccount,
    private val openUrlPreview: OpenUrlPreview,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignUpEventListener {

    val uiState: SignUpState = stateFactory.createSignUpState(createInitData())

    override fun onLearnMoreClick() {
        openUrlPreview(AuthConstants.PASSKEY_INFO_URL)
    }

    override fun onFirstNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.name.text = text
        uiState.name.isValid = validationResult.isValid
        uiState.name.isError = !validationResult.isValid
        uiState.name.supportingTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> AuthRes.strings.sign_up_first_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    override fun onLastNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.lastName.text = text
        uiState.lastName.isValid = validationResult.isValid
        uiState.lastName.isError = !validationResult.isValid
        uiState.lastName.supportingTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> AuthRes.strings.sign_up_last_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    override fun onEmailTextChanged(text: String) {
        val validationResult = validateEmail.invoke(text)
        uiState.email.text = text
        uiState.email.isValid = validationResult.isValid
        uiState.email.isError = !validationResult.isValid
        uiState.email.supportingTextRes = when (validationResult) {
            ValidateEmail.Result.Invalid.Format -> AuthRes.strings.sign_up_email_error.desc()
            ValidateEmail.Result.Valid -> null
        }
        validateButton()
    }

    override fun onConfirmButtonClick() {
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
            onError = {
                when (it) {
                    is Error.EmailAlreadyExist -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.supportingTextRes = AuthRes.strings.sign_up_email_exist.desc()
                    }
                    is Error.InvalidEmailFormat -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.supportingTextRes = AuthRes.strings.sign_up_email_error.desc()
                    }
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
            onTerminate = { uiState.confirmButton.isLoading = false },
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