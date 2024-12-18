package me.bookk.feature.authorization.presentation.sign_up.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.TextFieldState

interface SignUpState {
    val name: TextFieldState
    val lastName: TextFieldState
    val email: TextFieldState
    val phone: TextFieldState
    val businessName: TextFieldState
    val confirmButton: ButtonState

    class InitData(
        val nameHint: StringDesc,
        val lastNameHint: StringDesc,
        val emailHint: StringDesc,
        val phoneHint: StringDesc,
        val businessNameHint: StringDesc,
        val confirmButtonText: StringDesc
    )
}