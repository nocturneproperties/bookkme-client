package me.bookk.feature.authorization.presentation.screen.verifyemail

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transformLatest
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.authorization.domain.api.ConfirmEmailChange
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.presentation.AuthStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.math.ceil
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

class VerifyEmailViewModel(
    @InjectedParam initialRequest: VerifyEmailRequest,
    private val finishSignUp: FinishSignUp,
    private val resendSignUpCode: ResendSignUpCode,
    private val startEmailChange: StartEmailChange,
    private val confirmEmailChange: ConfirmEmailChange,
    private val clock: Clock,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private var request = initialRequest
    val uiState: VerifyEmailState = stateFactory.createVerifyEmailState().setup()
    private val resendAvailableAt = MutableStateFlow(request.resendAvailableAt)

    init {
        observeResendAvailability()
    }

    private fun VerifyEmailState.setup() = apply {
        appBar.title = AuthRes.strings.verify_email_title.desc()
        appBar.onBackClick = weakVMClosure { it.goBack() }
        codeSentMessage = request.descriptionResource().format(request.shownEmail)
        setupCode()
        setupButtons()
    }

    private fun VerifyEmailRequest.descriptionResource(): StringResource {
        return when (this) {
            is VerifyEmailRequest.SignUp -> AuthRes.strings.verify_email_description
            is VerifyEmailRequest.EmailChange -> AuthRes.strings.verify_email_change_description
        }
    }

    private fun VerifyEmailState.setupCode() {
        code.label = AuthRes.strings.verify_email_code_label.desc()
        code.placeholder = code.label
        code.inputType = InputType.DIGIT
        code.maxLength = CODE_LENGTH
        code.onTextChanged = weakVMClosure { vm, text -> vm.onCodeChanged(text) }
    }

    private fun VerifyEmailState.setupButtons() {
        confirmButton.text = AuthRes.strings.verify_email_confirm_button.desc()
        confirmButton.isEnabled = false
        confirmButton.onClick = weakVMClosure { it.submitCode() }
        resendButton.onClick = weakVMClosure { it.resendCode() }
        changeEmailButton.text = AuthRes.strings.verify_email_change_email_button.desc()
        changeEmailButton.onClick = weakVMClosure { it.goBack() }
    }

    private fun goBack() {
        uiState.navigation.push(VerifyEmailDestinations.Back)
    }

    private fun onCodeChanged(text: String) {
        val code = text.filter(Char::isDigit).take(CODE_LENGTH)
        uiState.code.text = code
        uiState.code.clearError()
        uiState.confirmButton.isEnabled = isCodeComplete()
        if (isCodeComplete()) {
            submitCode()
        }
    }

    private fun isCodeComplete(): Boolean {
        return uiState.code.text.length == CODE_LENGTH
    }

    private fun submitCode() {
        val code = uiState.code.text
        val request = request
        launch(
            key = VERIFICATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.confirmButton.startLoading() },
            call = { verify(request, code) },
            onComplete = { onVerified(request) },
            onError = ::onVerifyError,
            onTerminate = { uiState.confirmButton.stopLoading(enable = isCodeComplete()) },
        )
    }

    private suspend fun verify(request: VerifyEmailRequest, code: String) {
        when (request) {
            is VerifyEmailRequest.SignUp -> finishSignUp(code)

            is VerifyEmailRequest.EmailChange -> confirmEmailChange(
                request.pendingEmailChange,
                code,
            )
        }
    }

    private fun onVerified(request: VerifyEmailRequest) {
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
        if (request is VerifyEmailRequest.EmailChange) {
            uiState.notifications.add(
                PresentationNotification.GlobalMessage(
                    AuthRes.strings.verify_email_change_success.desc(),
                ),
            )
            uiState.navigation.push(VerifyEmailDestinations.EmailChanged)
        }
    }

    private fun onVerifyError(error: Throwable) {
        when (error) {
            is EmailVerificationError.CodeInvalid -> {
                rejectCode(AuthRes.strings.verify_email_code_invalid)
            }

            is EmailVerificationError.CodeExpired,
            is FinishSignUp.Error.ChallengeWindowExpired,
            is FinishSignUp.Error.SignUpNotStarted,
            -> {
                rejectCode(AuthRes.strings.verify_email_code_expired)
            }

            is EmailVerificationError.AttemptsExceeded -> {
                rejectCode(AuthRes.strings.verify_email_attempts_exceeded)
            }

            is Error.TooManyRequests -> {
                showCodeError(DesignSystem.strings.error_too_many_requests)
            }

            is ConfirmEmailChange.Error.EmailAlreadyExist -> {
                showCodeError(AuthRes.strings.verify_email_change_email_taken)
            }

            is FinishSignUp.Error.PasskeyVerificationFailed -> {
                failSignUp(AuthRes.strings.sign_up_passkey_failed)
            }

            is FinishSignUp.Error.AccountCreationFailed -> {
                failSignUp(AuthRes.strings.sign_up_failed)
            }

            is FinishSignUp.Error.EmailAlreadyExist,
            is FinishSignUp.Error.InvalidEmailFormat,
            is FinishSignUp.Error.LegalDocumentVersionUnknown,
            is FinishSignUp.Error.LegalDocumentsNotAccepted,
            -> {
                goBack()
            }

            else -> {
                uiState.notifications.add(error.notification())
            }
        }
    }

    private fun rejectCode(reason: StringResource) {
        uiState.code.text = ""
        showCodeError(reason)
    }

    private fun showCodeError(reason: StringResource) {
        uiState.code.showError(reason.desc())
    }

    private fun failSignUp(message: StringResource) {
        uiState.code.text = ""
        uiState.notifications.add(
            PresentationNotification.Message(
                message = message.desc(),
                buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc())),
            ),
        )
    }

    private fun resendCode() {
        val request = request
        launch(
            key = VERIFICATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.resendButton.startLoading() },
            call = { resend(request) },
            onComplete = ::onCodeResent,
            onError = ::onResendError,
            onTerminate = { uiState.resendButton.stopLoading(enable = !isResendWaiting()) },
        )
    }

    private suspend fun resend(request: VerifyEmailRequest): VerifyEmailRequest {
        return when (request) {
            is VerifyEmailRequest.SignUp -> {
                VerifyEmailRequest.SignUp(resendSignUpCode())
            }

            is VerifyEmailRequest.EmailChange -> {
                VerifyEmailRequest.EmailChange(startEmailChange(request.pendingEmailChange.email))
            }
        }
    }

    private fun onCodeResent(resent: VerifyEmailRequest) {
        request = resent
        uiState.code.text = ""
        uiState.code.clearError()
        uiState.confirmButton.isEnabled = false
        resendAvailableAt.value = resent.resendAvailableAt
    }

    private fun onResendError(error: Throwable) {
        when (error) {
            is EmailVerificationError.ResendTooSoon -> {
                showCodeError(AuthRes.strings.verify_email_resend_too_soon)
            }

            is Error.TooManyRequests -> {
                showCodeError(DesignSystem.strings.error_too_many_requests)
            }

            is StartEmailChange.Error.EmailAlreadyExist -> {
                showCodeError(AuthRes.strings.verify_email_change_email_taken)
            }

            is StartEmailChange.Error.InvalidEmailFormat -> {
                showCodeError(AuthRes.strings.sign_up_email_error)
            }

            is EmailVerificationError.Undeliverable -> {
                onUndeliverable()
            }

            is StartSignUp.Error.EmailAlreadyExist,
            is StartSignUp.Error.InvalidEmailFormat,
            is ResendSignUpCode.Error.SignUpNotStarted,
            -> {
                goBack()
            }

            else -> {
                uiState.notifications.add(error.notification())
            }
        }
    }

    private fun onUndeliverable() {
        when (request) {
            is VerifyEmailRequest.SignUp -> goBack()

            is VerifyEmailRequest.EmailChange -> showCodeError(
                AuthRes.strings.sign_up_email_undeliverable,
            )
        }
    }

    private fun isResendWaiting(): Boolean {
        return resendAvailableAt.value > clock.now()
    }

    private fun observeResendAvailability() {
        resendAvailableAt
            .transformLatest { emitCountdown(it) }
            .safeOnEach { renderResend(it) }
            .observe()
    }

    private suspend fun FlowCollector<Duration>.emitCountdown(availableAt: Instant) {
        var remaining = availableAt - clock.now()
        while (remaining.isPositive()) {
            emit(remaining)
            delay(remaining.untilNextWholeSecond())
            remaining = availableAt - clock.now()
        }
        emit(Duration.ZERO)
    }

    private fun Duration.untilNextWholeSecond(): Duration {
        val fraction = this - inWholeSeconds.seconds
        return if (fraction.isPositive()) fraction else 1.seconds
    }

    private fun renderResend(remaining: Duration) {
        val isWaiting = remaining.isPositive()
        uiState.resendButton.isEnabled = !isWaiting && !uiState.resendButton.isLoading
        uiState.resendButton.text = if (isWaiting) {
            AuthRes.strings.verify_email_resend_countdown.format(remaining.toCountdown())
        } else {
            AuthRes.strings.verify_email_resend_button.desc()
        }
    }

    private fun Duration.toCountdown(): String {
        val totalSeconds = ceil(toDouble(DurationUnit.SECONDS)).toLong()
        val seconds = (totalSeconds % SECONDS_IN_MINUTE).toString().padStart(2, '0')
        return "${totalSeconds / SECONDS_IN_MINUTE}:$seconds"
    }

    private companion object {
        const val CODE_LENGTH = 6
        const val SECONDS_IN_MINUTE = 60
        const val VERIFICATION_KEY = "verify_email"
    }
}
