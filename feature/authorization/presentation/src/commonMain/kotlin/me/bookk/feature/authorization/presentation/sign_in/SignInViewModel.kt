package me.bookk.feature.authorization.presentation.sign_in

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.presentation.AuthConstants
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_in.state.SignInEventListener
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState

class SignInViewModel(
    private val signIn: SignIn,
    private val deviceFacade: DeviceFacade,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignInEventListener {

    val uiState: SignInState = stateFactory.createSignInState(createInitData())

    override fun onBackClick() {
        uiState.navigation.push(SignInNavigationDestination.Main)
    }

    override fun onLearnMoreClick() {
        deviceFacade.openUrlPreview(AuthConstants.PASSKEY_INFO_URL)
    }

    override fun onSignInClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.signInButton.startLoading() },
            call = { signIn() },
            onError = {
                val message = if (it is SignIn.Error) {
                    val message = when (it) {
                        is SignIn.Error.NoAccountForThisPasskey -> AuthRes.strings.sign_in_error_no_account
                        is SignIn.Error.PasskeyVerificationFailed -> AuthRes.strings.sign_in_error_passkey_verification
                        is SignIn.Error.NoCredentialsAvailable -> AuthRes.strings.sign_in_error_no_passkeys_on_device
                    }.desc()
                    PresentationNotification.Message(
                        message = message,
                        buttons = listOf(
                            ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()),
                        )
                    )
                } else {
                    errorMapper.mapToNotification(it)
                }
                uiState.notification.add(message)
            },
            onTerminate = { uiState.signInButton.stopLoading() }
        )
    }

    companion object {

        fun createInitData() = SignInState.InitData(
            title = AuthRes.strings.sign_in_title.desc(),
            passkeyInfoCardData = PasskeyInfoCardData(
                title = AuthRes.strings.sign_in_passkey_why_title.desc(),
                description = AuthRes.strings.sign_in_passkey_why_description.desc()
            ),
            learnMoreText = AuthRes.strings.sign_in_passkey_learn_more_button.desc(),
            signInButtonText = AuthRes.strings.sign_in_passkey_button.desc(),
            signUpButtonText = AuthRes.strings.sign_in_sign_up_button.desc(),
            troubleshootButtonText = AuthRes.strings.sign_in_troubleshoot_button.desc()
        )
    }
}