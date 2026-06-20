package me.bookk.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import kotlinx.coroutines.launch
import me.bookk.designsystem.theme.typography.active
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation

@Composable
fun <T : PickerPresentation> OptionsMultiPicker(
    state: OptionsMultiPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T, () -> Unit) -> Unit
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Column(modifier) {
        Header(state.pickerTitle.localized())
        AppCard {
            Column(Modifier.animateContentSize()) {
                state.selectedItems.forEach {
                    itemContent(it) {
                        state.onItemsRemoveRequested(listOf(it))
                    }
                }
                if (state.isEditable) {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = { isDialogVisible = true }
                    ) {
                        Text(
                            state.addItemText.localized(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.titleSmall.active()
                        )
                    }
                }
            }
        }
    }
    if (isDialogVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        SelectorBottomSheet(
            sheetState = sheetState,
            title = state.pickerTitle,
            data = state.options,
            onItemPicked = {
                scope.launch {
                    sheetState.hide()
                    state.onItemsPicked(listOf(it))
                    isDialogVisible = false
                }
            },
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    isDialogVisible = false
                }
            },
        ) { _, item, isSelected, onClick ->
            StandardSelectorItem(
                item = item,
                isSelected = isSelected,
                stringify = { it.displayName.localized() },
                onClick = { onClick(item) }
            )
        }
    }
}