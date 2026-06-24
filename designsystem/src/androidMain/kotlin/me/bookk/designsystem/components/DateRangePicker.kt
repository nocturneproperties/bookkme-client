package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DateRangePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    state: DateRangePickerState,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    confirmButton: ButtonState = remember { AndroidButtonState(DesignSystem.strings.action_select.desc()) },
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    DesignSystemBottomSheet(
        sheetState = sheetState,
        title = state.title.localized(),
        onDismiss = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerField(
                state = state.startDate,
                modifier = Modifier.weight(1f)
            )
            DatePickerField(
                state = state.endDate,
                modifier = Modifier.weight(1f)
            )
        }
        ActionButton(
            modifier = Modifier.fillMaxWidth(),
            state = confirmButton,
            onClick = {
                scope.launch {
                    sheetState.hide()
                    state.onDateRangeSelected()
                    onDismiss()
                }
            }
        )
    }
}
