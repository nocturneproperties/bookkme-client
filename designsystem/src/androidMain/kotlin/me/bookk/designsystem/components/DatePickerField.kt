package me.bookk.designsystem.components

import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.bookk.core.presentation.date.SelectableDateRange
import me.bookk.designsystem.uistate.DatePickerFieldState

@Composable
fun DatePickerField(
    state: DatePickerFieldState,
    modifier: Modifier = Modifier,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = SelectableDateRange(state.maxDate, state.minDate)
    )
    var showDatePicker by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        state = state.textField,
        interactionSource = singleClickInteractionSource {
            showDatePicker = true
        }
    )

    if (showDatePicker) {
        AppDatePicker(
            selectedDate = state.pickedDate,
            datePickerState = datePickerState,
            onDismiss = { showDatePicker = false },
            onDatePicked = { state.onDatePicked?.invoke(it) }
        )
    }
}
