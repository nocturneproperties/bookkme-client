package me.bookk.feature.appointments.presentation.screen.create

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidDatePickerState
import me.bookk.designsystem.uistate.AndroidMultiPickerState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AndroidTimePickerFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerFieldState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot

internal class AndroidAppointmentCreateState : AppointmentCreateState {
    override val appBar: AppBarState = AndroidAppBarState()

    override val clientPicker = AndroidPickerFieldState<SimplePickerPresentation<ClientSnapshot>>()
    override val servicePicker = AndroidMultiPickerState<ServicePickerPresentation>()
    override val datePicker: DatePickerFieldState = AndroidDatePickerState()
    override val timePicker: TimePickerFieldState = AndroidTimePickerFieldState()
    override val note: TextFieldState = AndroidTextFieldState()
    override val create: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AppointmentCreateDestination> = AndroidNavigationState()
}
