package me.bookk.feature.services.presentation.service.add

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidCheckboxState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.CheckBoxState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.presentation.service.add.AddServiceState.GroupUI

internal class AndroidAddServiceState : AddServiceState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val group: PickerFieldState<GroupUI> = AndroidPickerFieldState()
    override val name: TextFieldState = AndroidTextFieldState()
    override val duration: TextFieldState = AndroidTextFieldState()
    override val price: TextFieldState = AndroidTextFieldState()
    override val enabled: CheckBoxState = AndroidCheckboxState()
    override val create: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AddServiceDestination> = AndroidNavigationState()
}