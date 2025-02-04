package me.bookk.feature.authorization.presentation.sign_in.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ErrorState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_in.SignInNavigationDestination

interface SignInState {
    val appBar: AppBarState
    val passkeyInfoCardData: PasskeyInfoCardData
    val learnMoreButton: ButtonState
    val signInButton: ButtonState
    val signUpButton: ButtonState
    val troubleshootButton: ButtonState

    val error: ErrorState
    val navigation: NavigationState<SignInNavigationDestination>

    class InitData(
        val title: StringDesc,
        val learnMoreText: StringDesc,
        val signInButtonText: StringDesc,
        val signUpButtonText: StringDesc,
        val troubleshootButtonText: StringDesc,
        val passkeyInfoCardData: PasskeyInfoCardData
    )
}