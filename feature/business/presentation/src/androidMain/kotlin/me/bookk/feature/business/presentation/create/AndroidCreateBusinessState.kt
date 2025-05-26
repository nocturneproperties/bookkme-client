package me.bookk.feature.business.presentation.create

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

@Immutable
internal class AndroidCreateBusinessState(
    initData: CreateBusinessState.InitData
) : CreateBusinessState {
    override val appBar: AppBarState = AndroidAppBarState(initData.title)
    override val name: TextFieldState = AndroidTextFieldState(
        hint = initData.hint,
        supportingTextRes = initData.supportingText,
        maxLength = initData.maxNameLength
    )
    override val createBtn: ButtonState = AndroidButtonState(
        text = initData.buttonText,
        isEnabled = false
    )

    override val notifications: PresentationNotificationState = AndroidNotificationState()
}