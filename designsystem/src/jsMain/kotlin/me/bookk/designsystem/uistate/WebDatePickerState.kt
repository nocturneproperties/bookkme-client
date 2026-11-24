package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import me.bookk.designsystem.uistate.DatePickerState

class WebDatePickerState : DatePickerState {
    override var isDatePickerVisible: Boolean by mutableStateOf(false)
    override var pickedDate: LocalDate? by mutableStateOf(null)
    override var maxDate: LocalDate? by mutableStateOf(null)
    override var minDate: LocalDate? by mutableStateOf(null)
    override var onDatePicked: ((LocalDate) -> Unit)? by mutableStateOf(null)
}
