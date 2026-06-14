package me.bookk.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalTime
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState

@Composable
fun AppTimePicker(
    selectedTime: LocalTime? = null,
    timePickerState: TimePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: 0,
        initialMinute = selectedTime?.minute ?: 0
    ),
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(
        containerColor = LocalColors.current.elevated,
    ),
    confirmButton: ButtonState = remember { AndroidButtonState(DesignSystem.strings.action_select.desc()) },
    dismissButton: ButtonState? = remember { AndroidButtonState(DesignSystem.strings.action_cancel.desc()) },
    onDismiss: () -> Unit,
    onTimePicked: (LocalTime) -> Unit
) {
    TimePickerDialog(
        title = {},
        onDismissRequest = onDismiss,
        dismissButton = dismissButton?.let { dismissButtonState ->
            {
                TextButton(
                    state = dismissButtonState,
                    onClick = onDismiss,
                )
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(end = 8.dp),
                state = confirmButton,
                onClick = {
                    onTimePicked(LocalTime(timePickerState.hour, timePickerState.minute))
                    onDismiss()
                }
            )
        }
    ) {
        TimePicker(state = timePickerState, colors = timePickerColors)
    }
}