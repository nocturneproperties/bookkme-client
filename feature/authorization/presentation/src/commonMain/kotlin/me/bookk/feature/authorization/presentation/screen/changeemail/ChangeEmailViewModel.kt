package me.bookk.feature.authorization.presentation.screen.changeemail

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import library.validation.api.ValidateEmail
import library.validation.api.ValidateEmail.Result.Invalid.Format.isValid
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.presentation.AuthStateFactory

class ChangeEmailViewModel(
    private val startEmailChange: StartEmailChange,
    private val validateEmail: ValidateEmail,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: ChangeEmailState = stateFactory.createChangeEmailState().setup()

    private fun ChangeEmailState.setup() = apply {
        appBar.title = AuthRes.strings.change_email_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(ChangeEmailDestinations.Back) }
        explanation = AuthRes.strings.change_email_explanation.desc()
        email.label = AuthRes.strings.change_email_field_label.desc()
        email.placeholder = email.label
        email.inputType = InputType.EMAIL
        email.onTextChanged = weakVMClosure { vm, text -> vm.onEmailChanged(text) }
        continueButton.text = AuthRes.strings.change_email_continue_button.desc()
        continueButton.isEnabled = false
        continueButton.onClick = weakVMClosure { it.submit() }
    }

    private fun onEmailChanged(text: String) {
        uiState.email.text = text
        uiState.email.clearError()
        uiState.continueButton.isEnabled = text.isNotBlank()
    }

    private fun submit() {
        val email = uiState.email.text
        if (!validateEmail(email.trim()).isValid) {
            showEmailError(AuthRes.strings.sign_up_email_error)
            return
        }
        launch(
            key = START_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.continueButton.startLoading() },
            call = { startEmailChange(email) },
            onComplete = { uiState.navigation.push(ChangeEmailDestinations.VerifyEmail(it)) },
            onError = ::onStartError,
            onTerminate = { uiState.continueButton.stopLoading() },
        )
    }

    private fun onStartError(error: Throwable) {
        when (error) {
            is StartEmailChange.Error.EmailAlreadyExist -> showEmailError(AuthRes.strings.sign_up_email_exist)
            is StartEmailChange.Error.InvalidEmailFormat -> showEmailError(AuthRes.strings.sign_up_email_error)
            is EmailVerificationError.Undeliverable -> showEmailError(AuthRes.strings.sign_up_email_undeliverable)
            is EmailVerificationError.ResendTooSoon -> showEmailError(AuthRes.strings.verify_email_resend_too_soon)
            is Error.TooManyRequests -> showEmailError(DesignSystem.strings.error_too_many_requests)
            else -> uiState.notifications.add(error.notification())
        }
    }

    private fun showEmailError(message: StringResource) {
        uiState.email.showError(message.desc())
    }

    private companion object {
        const val START_KEY = "start_email_change"
    }
}
