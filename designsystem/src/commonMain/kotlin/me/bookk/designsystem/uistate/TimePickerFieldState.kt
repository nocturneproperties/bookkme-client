package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalTime

interface TimePickerFieldState : ViewState {
    val textField: TextFieldState
    var pickedTime: LocalTime?
    var onTimePicked: ((LocalTime) -> Unit)?
}