package me.bookk.feature.services.presentation.service.edit

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.presentation.service.ServiceGroupOption

interface EditServiceState {
    val appBar: AppBarState

    val group: PickerFieldState<ServiceGroupOption>
    val name: TextFieldState
    val duration: TextFieldState
    val price: TextFieldState
    val available: BooleanState

    val submit: ButtonState
    val deleteButton: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<EditServiceDestination>
}
