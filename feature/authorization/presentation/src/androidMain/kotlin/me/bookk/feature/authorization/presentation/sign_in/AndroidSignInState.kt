package me.bookk.feature.authorization.presentation.sign_in

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ViewState
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_in.state.TroubleshootCardData

@Immutable
class AndroidSignInState private constructor(
    override val appBar: AppBarState,
    override val troubleshootCardStaticData: TroubleshootCardData,
    override val troubleshootView: ViewState,
    override val learnMoreButton: ButtonState,
    override val signInButton: ButtonState
) : SignInState {
    constructor(initData: SignInState.InitData) : this(
        appBar = AndroidAppBarState(title = initData.title),
        troubleshootCardStaticData = initData.troubleshootCardStaticData,
        troubleshootView = AndroidViewState(initData.isTroubleshootCardVisible),
        learnMoreButton = AndroidButtonState(text = initData.learnMoreText),
        signInButton = AndroidButtonState(text = initData.buttonText),
    )
}