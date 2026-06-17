package me.bookk.designsystem.uistate

class AndroidTimePickerFieldState : AndroidViewState(), TimePickerFieldState {
    override val textField: TextFieldState = AndroidTextFieldState(readOnly = true)
    override val timePicker: TimePickerState = AndroidTimePickerState()
}