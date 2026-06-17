package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import me.bookk.core.now
import me.bookk.designsystem.uistate.AndroidDatePickerState
import me.bookk.designsystem.uistate.AndroidTimePickerState
import me.bookk.designsystem.uistate.DateTimePickerState

@Composable
fun DateTimePicker(
    state: DateTimePickerState
) {
    val timePickerState = remember {
        AndroidTimePickerState(
            isTimePickerVisible = false,
            pickedTime = state.pickedDate?.time,
            onTimePicked = {
                val picked = state.pickedDate?.date?.atTime(it) ?: LocalDate.now().atTime(it)
                state.onDatePicked?.invoke(picked)
                state.isDatePickerVisible = false
            }
        )
    }
    val datePickerState = remember {
        AndroidDatePickerState(
            isDatePickerVisible = true,
            pickedDate = state.pickedDate?.date,
            maxDate = state.maxDate?.date,
            minDate = state.minDate?.date,
            onDatePicked = {
                state.pickedDate = it.atTime(state.pickedDate?.time ?: LocalTime.now())
                timePickerState.isTimePickerVisible = true
            }
        )
    }

    if (datePickerState.isDatePickerVisible) {
        AppDatePicker(state = datePickerState)
    }
    if (timePickerState.isTimePickerVisible) {
        AppTimePicker(state = timePickerState)
    }

    LaunchedEffect(datePickerState.isDatePickerVisible, timePickerState.isTimePickerVisible) {
        val dismissedWithoutPicking = !timePickerState.isTimePickerVisible && !datePickerState.isDatePickerVisible
        if (dismissedWithoutPicking) {
            state.isDatePickerVisible = false
        }
    }
}
