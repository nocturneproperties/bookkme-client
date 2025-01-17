package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow
import me.bookk.android.feature.sign_up.resources.SignUpRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationError.Message
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.Error
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.api.isValid
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
    val navigationFlow = MutableSharedFlow<SignUpNavigationEvent>()

    override fun onFirstNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.name.text = text
        uiState.name.isValid = validationResult.isValid
        uiState.name.isError = !validationResult.isValid
        uiState.name.errorTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> SignUpRes.strings.sign_up_first_name_error.desc()
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
            ValidateName.Result.Invalid.Length -> SignUpRes.strings.sign_up_last_name_error.desc()
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
            ValidateEmail.Result.Invalid.Format -> SignUpRes.strings.sign_up_email_error.desc()
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
                navigationFlow.emit(SignUpNavigationEvent.ToMain)
            },
            onError = {
                when (it) {
                    is Error.EmailAlreadyExist -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.errorTextRes = SignUpRes.strings.sign_up_email_exist.desc()
                    }
                    is Error.InvalidEmailFormat -> {
                        uiState.email.isError = true
                        uiState.email.isValid = false
                        uiState.email.errorTextRes = SignUpRes.strings.sign_up_email_error.desc()
                    }
                    is Error.PasskeyVerificationFailed -> {
                        errorFlow.emit(Message(SignUpRes.strings.sign_up_passkey_failed.desc()))
                    }
                    is Error.AccountCreationFailed -> {
                        errorFlow.emit(Message(SignUpRes.strings.sign_up_failed.desc()))
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
            title = SignUpRes.strings.sign_up_create_acc.desc(),
            nameHint = SignUpRes.strings.sign_up_first_name.desc(),
            lastNameHint = SignUpRes.strings.sign_up_last_name.desc(),
            emailHint = SignUpRes.strings.sign_up_email.desc(),
            confirmButtonText = SignUpRes.strings.sign_up_create_account_button.desc()
        )
    }
}