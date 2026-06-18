package me.bookk.designsystem.uistate

class AndroidDatePickerFieldState(
    textFieldState: TextFieldState = AndroidTextFieldState(readOnly = true),
) : AndroidViewState(isVisible = true), DatePickerFieldState {
    override val textField: TextFieldState = textFieldState
    override val datePicker: DatePickerState = AndroidDatePickerState()
}