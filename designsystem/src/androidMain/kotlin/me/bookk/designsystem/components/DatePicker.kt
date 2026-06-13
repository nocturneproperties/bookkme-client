package me.bookk.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState
import kotlin.time.Instant

@Composable
fun AppDatePicker(
    selectedDate: LocalDate? = null,
    datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDate = selectedDate?.toJavaLocalDate()
    ),
    datePickerColors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = LocalColors.current.elevated,
        selectedDayContentColor = LocalColors.current.primaryText,
        selectedDayContainerColor = LocalColors.current.actionText,
        todayDateBorderColor = LocalColors.current.actionText
    ),
    confirmButton: ButtonState = remember { AndroidButtonState(DesignSystem.strings.action_select.desc()) },
    dismissButton: ButtonState? = remember { AndroidButtonState(DesignSystem.strings.action_cancel.desc()) },
    onDismiss: () -> Unit,
    onDatePicked: (LocalDate) -> Unit
) {
    DatePickerDialog(
        colors = datePickerColors,
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
                    datePickerState.selectedDateMillis?.let { timestamp ->
                        val date = Instant.fromEpochMilliseconds(timestamp)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        onDatePicked(date)
                    }
                    onDismiss()
                }
            )
        }
    ) {
        DatePicker(state = datePickerState, colors = datePickerColors)
    }
}