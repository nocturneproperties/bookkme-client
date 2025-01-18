package me.bookk.feature.authorization.presentation.sign_in

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.AppBarStateImpl
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ButtonStateImpl
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_in.state.TroubleshootPasskeyCardData

@Immutable
class AndroidSignInState private constructor(
    override val appBar: AppBarState,
    override val passkeyCard: TroubleshootPasskeyCardData,
    override val learnMoreButton: ButtonState,
    override val signInButton: ButtonState
) : SignInState {
    constructor(initData: SignInState.InitData) : this(
        appBar = AppBarStateImpl(title = initData.title),
        passkeyCard = initData.passkeyCardData,
        learnMoreButton = ButtonStateImpl(text = initData.learnMoreText),
        signInButton = ButtonStateImpl(text = initData.buttonText),
    )
}