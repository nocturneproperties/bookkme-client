package me.bookk.designsystem.uistate

interface DatePickerFieldState : ViewState {
    val textField: TextFieldState
    val datePicker: DatePickerState
}