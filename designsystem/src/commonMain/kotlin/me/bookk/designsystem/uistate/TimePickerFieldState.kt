package me.bookk.designsystem.uistate

interface TimePickerFieldState : ViewState {
    val textField: TextFieldState
    val timePicker: TimePickerState
}