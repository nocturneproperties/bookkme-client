package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.bookk.designsystem.uistate.TimePickerFieldState

@Composable
fun TimePickerField(
    state: TimePickerFieldState,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        state = state.textField,
        interactionSource = singleClickInteractionSource {
            showTimePicker = true
        }
    )

    if (showTimePicker) {
        AppTimePicker(
            selectedTime = state.pickedTime,
            onDismiss = { showTimePicker = false },
            onTimePicked = { state.onTimePicked?.invoke(it) }
        )
    }
}