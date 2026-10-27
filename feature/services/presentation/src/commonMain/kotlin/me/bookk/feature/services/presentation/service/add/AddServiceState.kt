package me.bookk.feature.services.presentation.service.add

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.ViewState
import me.bookk.feature.services.presentation.service.ServiceGroupOption

interface AddServiceState : ViewState {

    val appBar: AppBarState

    val group: PickerFieldState<ServiceGroupOption>
    val name: TextFieldState
    val duration: TextFieldState
    val price: TextFieldState
    val enabled: BooleanState
    val create: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AddServiceDestination>
}