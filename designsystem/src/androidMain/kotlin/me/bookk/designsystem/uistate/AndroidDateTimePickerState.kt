package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDateTime

class AndroidDateTimePickerState(
    isDatePickerVisible: Boolean = false,
    pickedDate: LocalDateTime? = null,
    maxDate: LocalDateTime? = null,
    minDate: LocalDateTime? = null,
    onDatePicked: ((LocalDateTime) -> Unit)? = null
) : DateTimePickerState {
    override var isDatePickerVisible: Boolean by mutableStateOf(isDatePickerVisible)
    override var pickedDate: LocalDateTime? by mutableStateOf(pickedDate)
    override var maxDate: LocalDateTime? by mutableStateOf(maxDate)
    override var minDate: LocalDateTime? by mutableStateOf(minDate)
    override var onDatePicked: ((LocalDateTime) -> Unit)? by mutableStateOf(onDatePicked)
}
