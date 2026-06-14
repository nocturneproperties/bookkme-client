package me.bookk.feature.appointments.presentation.screen.create

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerFieldState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot

interface AppointmentCreateState {
    val appBar: AppBarState

    val clientPicker: PickerFieldState<SimplePickerPresentation<ClientSnapshot>>
    val servicePickers: ListState<PickerFieldState<SimplePickerPresentation<ServiceSnapshot>>>
    val datePicker: DatePickerFieldState
    val timePicker: TimePickerFieldState
    val note: TextFieldState

    val create: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentCreateDestination>
}
