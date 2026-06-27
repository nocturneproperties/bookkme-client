package me.bookk.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import me.bookk.core.presentation.date.SelectableDateRange
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState
import kotlin.time.Instant
import me.bookk.designsystem.uistate.DatePickerState as AppDatePickerState

@Composable
fun AppDatePicker(
    state: AppDatePickerState,
    datePickerColors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = LocalColors.current.elevated,
        selectedDayContentColor = LocalColors.current.primaryText,
        selectedDayContainerColor = LocalColors.current.actionText,
        todayDateBorderColor = LocalColors.current.actionText
    ),
    confirmButton: ButtonState = remember { AndroidButtonState(DesignSystem.strings.action_select.desc()) },
    dismissButton: ButtonState? = remember { AndroidButtonState(DesignSystem.strings.action_cancel.desc()) },
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = state.pickedDate?.toJavaLocalDate(),
        selectableDates = SelectableDateRange(state.maxDate, state.minDate)
    )
    DatePickerDialog(
        colors = datePickerColors,
        onDismissRequest = { state.isDatePickerVisible = false },
        dismissButton = dismissButton?.let { dismissButtonState ->
            {
                StateTextButton(
                    state = dismissButtonState,
                    onClick = { state.isDatePickerVisible = false },
                )
            }
        },
        confirmButton = {
            StateTextButton(
                modifier = Modifier.padding(end = 8.dp),
                state = confirmButton,
                onClick = {
                    datePickerState.selectedDateMillis?.let { timestamp ->
                        val date = Instant.fromEpochMilliseconds(timestamp)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        state.onDatePicked?.invoke(date)
                    }
                    state.isDatePickerVisible = false
                }
            )
        }
    ) {
        DatePicker(state = datePickerState, colors = datePickerColors)
    }
}