package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.AppBarStateImpl
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ButtonStateImpl
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TextFieldStateImpl
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Immutable
class AndroidSignUpState private constructor(
    override val appBar: AppBarState,
    override val name: TextFieldState,
    override val lastName: TextFieldState,
    override val email: TextFieldState,
    override val confirmButton: ButtonState,
) : SignUpState {

    constructor(initData: SignUpState.InitData) : this(
        appBar = AppBarStateImpl(title = initData.title),
        name = TextFieldStateImpl(hint = initData.nameHint),
        lastName = TextFieldStateImpl(hint = initData.lastNameHint),
        email = TextFieldStateImpl(hint = initData.emailHint),
        confirmButton = ButtonStateImpl(text = initData.confirmButtonText, isEnabled = false),
    )
}