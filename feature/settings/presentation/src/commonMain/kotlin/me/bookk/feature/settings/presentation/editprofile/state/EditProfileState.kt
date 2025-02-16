package me.bookk.feature.settings.presentation.editprofile.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface EditProfileState {
    val appBar: AppBarState
    val name: TextFieldState
    val lastName: TextFieldState
    val email: TextFieldState
    val confirmButton: ButtonState

    val notification: PresentationNotificationState

    class InitData(
        val title: StringDesc,
        val nameHint: StringDesc,
        val lastNameHint: StringDesc,
        val emailHint: StringDesc,
        val confirmButtonText: StringDesc
    )
}