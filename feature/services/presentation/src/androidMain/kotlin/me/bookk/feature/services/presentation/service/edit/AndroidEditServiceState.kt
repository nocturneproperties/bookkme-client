package me.bookk.feature.services.presentation.service.edit

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.presentation.service.ServiceGroupOption

internal class AndroidEditServiceState : EditServiceState {
    override val appBar: AppBarState = AndroidAppBarState()

    override val group: PickerFieldState<ServiceGroupOption> = AndroidPickerFieldState()
    override val name: TextFieldState = AndroidTextFieldState()
    override val duration: TextFieldState = AndroidTextFieldState()
    override val price: TextFieldState = AndroidTextFieldState()
    override val available: BooleanState = AndroidBooleanState()

    override val submit: ButtonState = AndroidButtonState()
    override val deleteButton: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EditServiceDestination> = AndroidNavigationState()
}
