package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationError.Message
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.Error
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateEmail.Result.Invalid.Format.isValid
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

class SignUpViewModel(
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val createAccount: CreateAccount,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignUpEventListener {

    val uiState: SignUpState = stateFactory.createSignUpState(createInitData())

    override fun onBackClick() {
        uiState.navigation.navigationDestination = SignUpNavigationDestination.Back
    }

    override fun onFirstNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.name.text = text
        uiState.name.isValid = validationResult.isValid
        uiState.name.isError = !validationResult.isValid
        uiState.name.errorTextRes = when (validationResult) {
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
        uiState.lastName.errorTextRes = when (validationResult) {
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
        uiState.email.errorTextRes = when (validationResult) {
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
            onComplete = {
                uiState.navigation.navigationDestination = SignUpNavigationDestination.ToMain
            },
            onError = {
                when (it) {
                    is Error.EmailAlreadyExist -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.errorTextRes = AuthRes.strings.sign_up_email_exist.desc()
                    }
                    is Error.InvalidEmailFormat -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.errorTextRes = AuthRes.strings.sign_up_email_error.desc()
                    }
                    is Error.PasskeyVerificationFailed -> {
                        uiState.error.add(
                            Message(
                                message = AuthRes.strings.sign_up_passkey_failed.desc(),
                                buttonText = DesignSystem.strings.action_ok.desc()
                            )
                        )
                    }
                    is Error.AccountCreationFailed -> {
                        uiState.error.add(
                            Message(
                                message = AuthRes.strings.sign_up_failed.desc(),
                                buttonText = DesignSystem.strings.action_ok.desc()
                            )
                        )
                    }
                    else -> throw it
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
            confirmButtonText = AuthRes.strings.sign_up_create_account_button.desc()
        )
    }
}