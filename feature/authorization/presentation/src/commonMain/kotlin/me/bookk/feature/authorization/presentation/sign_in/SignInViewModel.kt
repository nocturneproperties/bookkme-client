package me.bookk.feature.authorization.presentation.sign_in

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationError
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_in.state.SignInEventListener
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_in.state.TroubleshootCardData
import me.bookk.feature.platform.domain.api.OpenUrlPreview

class SignInViewModel(
    private val signIn: SignIn,
    private val openUrlPreview: OpenUrlPreview,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignInEventListener {

    val uiState: SignInState = stateFactory.createSignInState(createInitData())

    override fun onBackClick() {
        uiState.navigation.navigationDestination = SignInNavigationDestination.ToMain
    }

    override fun onLearnMoreClick() {
        openUrlPreview(PASSKEY_INFO_URL)
    }

    override fun onSignUpClick() {
        uiState.navigation.navigationDestination = SignInNavigationDestination.ToSignUp
    }

    override fun onSignInClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.signInButton.isLoading = true },
            call = { signIn() },
            onComplete = {
                uiState.navigation.navigationDestination = SignInNavigationDestination.ToMain
            },
            onError = {
                uiState.troubleshootView.isVisible = true
                val message = when (it) {
                    is SignIn.Error.NoAccountForThisPasskey -> AuthRes.strings.sign_in_error_no_account
                    is SignIn.Error.PasskeyVerificationFailed -> AuthRes.strings.sign_in_error_passkey_verification
                    is SignIn.Error.NoCredentialsAvailable -> AuthRes.strings.sign_in_error_no_passkeys_on_device
                    else -> throw it
                }.desc()
                uiState.error.add(
                    PresentationError.Message(
                        message = message,
                        buttonText = DesignSystem.strings.action_ok.desc()
                    )
                )
            },
            onTerminate = { uiState.signInButton.isLoading = false }
        )
    }

    companion object {

        private const val PASSKEY_INFO_URL = "https://support.apple.com/102195"

        fun createInitData() = SignInState.InitData(
            title = AuthRes.strings.sign_in_title.desc(),
            troubleshootCardStaticData = TroubleshootCardData(
                title = AuthRes.strings.sign_in_passkey_troubleshoot.desc(),
                icon = AuthRes.images.passkey,
                reasons = listOf(
                    TroubleshootCardData.Reason(
                        id = 0,
                        title = AuthRes.strings.sign_in_passkey_lost.desc(),
                        description = AuthRes.strings.sign_in_passkey_lost_description.desc()
                    ),
                    TroubleshootCardData.Reason(
                        id = 1,
                        title = AuthRes.strings.sign_in_passkey_missing.desc(),
                        description = AuthRes.strings.sign_in_passkey_missing_description.desc()
                    )
                )
            ),
            isTroubleshootCardVisible = false,
            learnMoreText = AuthRes.strings.sign_in_passkey_learn_more_button.desc(),
            signInButtonText = AuthRes.strings.sign_in_passkey_button.desc(),
            signUpButtonText = AuthRes.strings.sign_in_sign_up_button.desc()
        )
    }
}