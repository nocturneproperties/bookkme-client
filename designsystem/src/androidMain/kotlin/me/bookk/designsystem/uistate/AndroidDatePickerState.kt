package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate

class AndroidDatePickerState(
    isDatePickerVisible: Boolean = false,
    pickedDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    minDate: LocalDate? = null,
    onDatePicked: ((LocalDate) -> Unit)? = null
) : DatePickerState {
    override var isDatePickerVisible: Boolean by mutableStateOf(isDatePickerVisible)
    override var pickedDate: LocalDate? by mutableStateOf(pickedDate)
    override var maxDate: LocalDate? by mutableStateOf(maxDate)
    override var minDate: LocalDate? by mutableStateOf(minDate)
    override var onDatePicked: ((LocalDate) -> Unit)? by mutableStateOf(onDatePicked)
}
