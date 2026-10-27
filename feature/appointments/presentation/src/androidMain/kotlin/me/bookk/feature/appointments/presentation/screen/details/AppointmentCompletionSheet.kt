package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import library.picker.standardOptionsScreenPicker
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.DesignSystemBottomSheet
import me.bookk.designsystem.components.OptionsMultiPicker
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.feature.appointments.presentation.screen.create.ServiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentCompletionSheet(state: AppointmentCompletionState) {
    if (!state.isSheetVisible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DesignSystemBottomSheet(
        sheetState = sheetState,
        title = state.title.localized(),
        onDismiss = { state.isSheetVisible = false }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionsMultiPicker(
                    state.servicePicker,
                    showDividers = true,
                    screenPicker = standardOptionsScreenPicker()
                ) { item, onItemRemove ->
                    ServiceItem(item, onItemRemove)
                }
                Text(
                    text = state.servicesHint.localized(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall.secondary()
                )
            }
            TextField(state.price, singleLine = true)
            TextField(state.note, minLines = 3)
            ActionButton(state.confirmButton, Modifier.fillMaxWidth())
        }
    }
}
