package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.uistate.TimePickerFieldState

@Composable
fun TimePickerField(
    state: TimePickerFieldState,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        state = state.textField,
        interactionSource = singleClickInteractionSource {
            state.timePicker.isTimePickerVisible = true
        }
    )

    if (state.timePicker.isTimePickerVisible) {
        AppTimePicker(state = state.timePicker)
    }
}