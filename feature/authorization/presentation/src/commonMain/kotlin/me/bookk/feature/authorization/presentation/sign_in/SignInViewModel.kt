package me.bookk.feature.authorization.presentation.sign_in

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_in.state.Reason
import me.bookk.feature.authorization.presentation.sign_in.state.SignInEventListener
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_in.state.TroubleshootPasskeyCardData

class SignInViewModel(
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignInEventListener {

    val uiState: SignInState = stateFactory.createSignInState(createInitData())
    val navigationFlow = MutableSharedFlow<SignInNavigationEvent>()

    override fun onLearnMoreClick() {

    }

    override fun onSignInClick() {

    }

    companion object {

        fun createInitData() = SignInState.InitData(
            title = AuthRes.strings.sign_in_title.desc(),
            passkeyCardData = TroubleshootPasskeyCardData(
                title = AuthRes.strings.sign_in_passkey_troubleshoot.desc(),
                icon = AuthRes.images.passkey,
                reasons = listOf(
                    Reason(
                        title = AuthRes.strings.sign_in_passkey_lost.desc(),
                        description = AuthRes.strings.sign_in_passkey_lost_description.desc()
                    ),
                    Reason(
                        title = AuthRes.strings.sign_in_passkey_missing.desc(),
                        description = AuthRes.strings.sign_in_passkey_missing_description.desc()
                    )
                )
            ),
            learnMoreText = AuthRes.strings.sign_in_passkey_learn_more_button.desc(),
            buttonText = AuthRes.strings.sign_in_passkey_button.desc()
        )
    }
}