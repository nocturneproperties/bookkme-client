package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalTime

class AndroidTimePickerState(
    isTimePickerVisible: Boolean = false,
    pickedTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    minTime: LocalTime? = null,
    onTimePicked: ((LocalTime) -> Unit)? = null
) : TimePickerState {
    override var isTimePickerVisible: Boolean by mutableStateOf(isTimePickerVisible)
    override var maxTime: LocalTime? by mutableStateOf(maxTime)
    override var minTime: LocalTime? by mutableStateOf(minTime)
    override var pickedTime: LocalTime? by mutableStateOf(pickedTime)
    override var onTimePicked: ((LocalTime) -> Unit)? by mutableStateOf(onTimePicked)
}
