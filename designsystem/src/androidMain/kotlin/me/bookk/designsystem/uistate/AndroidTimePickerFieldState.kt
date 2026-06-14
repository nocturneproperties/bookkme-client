package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalTime

class AndroidTimePickerFieldState : AndroidViewState(), TimePickerFieldState {
    override val textField: TextFieldState = AndroidTextFieldState(readOnly = true)
    override var pickedTime: LocalTime? by mutableStateOf(null)
    override var onTimePicked: ((LocalTime) -> Unit)? by mutableStateOf(null)
}