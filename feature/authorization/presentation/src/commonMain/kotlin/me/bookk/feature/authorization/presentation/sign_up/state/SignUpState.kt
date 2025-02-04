package me.bookk.feature.authorization.presentation.sign_up.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ErrorState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination

interface SignUpState {
    val appBar: AppBarState
    val name: TextFieldState
    val lastName: TextFieldState
    val email: TextFieldState
    val passkeyInfoCardData: PasskeyInfoCardData
    val learnMoreButton: ButtonState
    val confirmButton: ButtonState

    val error: ErrorState
    val navigation: NavigationState<SignUpNavigationDestination>

    class InitData(
        val title: StringDesc,
        val nameHint: StringDesc,
        val lastNameHint: StringDesc,
        val emailHint: StringDesc,
        val confirmButtonText: StringDesc,
        val passkeyInfoCardData: PasskeyInfoCardData,
        val learnMoreButtonText: StringDesc
    )
}