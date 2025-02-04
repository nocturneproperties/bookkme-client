package me.bookk.feature.authorization.presentation.sign_in

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidErrorState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ErrorState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState

@Immutable
internal class AndroidSignInState private constructor(
    override val appBar: AppBarState,
    override val passkeyInfoCardData: PasskeyInfoCardData,
    override val learnMoreButton: ButtonState,
    override val signInButton: ButtonState,
    override val signUpButton: ButtonState,
    override val troubleshootButton: ButtonState,
    override val error: ErrorState,
    override val navigation: NavigationState<SignInNavigationDestination>
) : SignInState {

    constructor(initData: SignInState.InitData) : this(
        appBar = AndroidAppBarState(title = initData.title),
        passkeyInfoCardData = initData.passkeyInfoCardData,
        learnMoreButton = AndroidButtonState(text = initData.learnMoreText),
        signInButton = AndroidButtonState(text = initData.signInButtonText),
        signUpButton = AndroidButtonState(text = initData.signUpButtonText),
        troubleshootButton = AndroidButtonState(text = initData.troubleshootButtonText),
        error = AndroidErrorState(),
        navigation = AndroidNavigationState<SignInNavigationDestination>()
    )
}