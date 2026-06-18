package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.uistate.DatePickerFieldState

@Composable
fun DatePickerField(
    state: DatePickerFieldState,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        state = state.textField,
        interactionSource = singleClickInteractionSource {
            state.datePicker.isDatePickerVisible = true
        }
    )

    if (state.datePicker.isDatePickerVisible) {
        AppDatePicker(state.datePicker)
    }
}
