package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidErrorState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ErrorState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Immutable
internal class AndroidSignUpState private constructor(
    override val appBar: AppBarState,
    override val name: TextFieldState,
    override val lastName: TextFieldState,
    override val email: TextFieldState,
    override val confirmButton: ButtonState,
    override val error: ErrorState,
    override val navigation: NavigationState<SignUpNavigationDestination>
) : SignUpState {

    constructor(initData: SignUpState.InitData) : this(
        appBar = AndroidAppBarState(title = initData.title),
        name = AndroidTextFieldState(hint = initData.nameHint),
        lastName = AndroidTextFieldState(hint = initData.lastNameHint),
        email = AndroidTextFieldState(hint = initData.emailHint),
        confirmButton = AndroidButtonState(text = initData.confirmButtonText, isEnabled = false),
        error = AndroidErrorState(),
        navigation = AndroidNavigationState<SignUpNavigationDestination>()
    )
}