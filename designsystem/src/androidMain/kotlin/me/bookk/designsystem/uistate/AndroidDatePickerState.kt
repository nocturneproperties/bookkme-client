package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate

class AndroidDatePickerState(
    textFieldState: TextFieldState = AndroidTextFieldState(readOnly = true),
    pickedDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    minDate: LocalDate? = null,
    override var onDatePicked: ((LocalDate) -> Unit)? = null
) : AndroidViewState(isVisible = true), DatePickerFieldState {
    override val textField: TextFieldState = textFieldState
    override var maxDate: LocalDate? by mutableStateOf(maxDate)
    override var minDate: LocalDate? by mutableStateOf(minDate)
    override var pickedDate: LocalDate? by mutableStateOf(pickedDate)
}