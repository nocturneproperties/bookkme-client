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
import me.bookk.designsystem.uistate.TimePickerState as AppTimePickerState

@Composable
fun AppTimePicker(
    state: AppTimePickerState,
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(
        containerColor = LocalColors.current.elevated,
        timeSelectorSelectedContainerColor = LocalColors.current.actionText
    ),
    confirmButton: ButtonState = remember { AndroidButtonState(DesignSystem.strings.action_select.desc()) },
    dismissButton: ButtonState? = remember { AndroidButtonState(DesignSystem.strings.action_cancel.desc()) },
) {
    val timePickerState: TimePickerState = rememberTimePickerState(
        initialHour = state.pickedTime?.hour ?: 0,
        initialMinute = state.pickedTime?.minute ?: 0
    )
    TimePickerDialog(
        title = {},
        onDismissRequest = { state.isTimePickerVisible = false },
        dismissButton = dismissButton?.let { dismissButtonState ->
            {
                StateTextButton(
                    state = dismissButtonState,
                    onClick = { state.isTimePickerVisible = false },
                )
            }
        },
        confirmButton = {
            StateTextButton(
                modifier = Modifier.padding(end = 8.dp),
                state = confirmButton,
                onClick = {
                    state.onTimePicked?.invoke(LocalTime(timePickerState.hour, timePickerState.minute))
                    state.isTimePickerVisible = false
                }
            )
        }
    ) {
        TimePicker(state = timePickerState, colors = timePickerColors)
    }
}