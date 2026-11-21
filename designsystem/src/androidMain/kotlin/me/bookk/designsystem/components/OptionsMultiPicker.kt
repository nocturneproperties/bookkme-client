package me.bookk.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import kotlinx.coroutines.launch
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.active
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickedItem
import me.bookk.designsystem.uistate.PickerFieldState.PickerType
import me.bookk.designsystem.uistate.PickerPresentation

@Composable
fun <T : PickerPresentation> OptionsMultiPicker(
    state: OptionsMultiPickerState<T>,
    modifier: Modifier = Modifier,
    showDividers: Boolean = false,
    screenPicker: @Composable (
        state: OptionsMultiPickerState<T>,
        onDismiss: () -> Unit,
        onItemsPicked: (List<T>) -> Unit,
    ) -> Unit = { _, _, _ -> },
    itemContent: @Composable (T, onItemRemove: (() -> Unit)?) -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Column(modifier) {
        Header(state.pickerTitle.localized())
        AppCard {
            Column(Modifier.animateContentSize()) {
                val placeholder = state.placeholder
                if (state.selectedItems.isEmpty() && placeholder != null) {
                    Text(
                        placeholder.localized(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.secondary(),
                    )
                }
                state.selectedItems.forEachIndexed { index, picked ->
                    PickedItemRow(state, picked, itemContent)
                    if (showDividers && index < state.selectedItems.lastIndex) {
                        HorizontalDivider(color = LocalColors.current.divider)
                    }
                }
                if (state.isEditable) {
                    HorizontalDivider(color = LocalColors.current.divider)
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = { isDialogVisible = true },
                    ) {
                        Text(
                            state.addItemText.localized(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.active(),
                        )
                    }
                }
            }
        }
    }
    if (isDialogVisible && state.pickerType == PickerType.SCREEN) {
        screenPicker(
            state,
            { isDialogVisible = false },
            {
                state.onItemsPicked(it)
                isDialogVisible = false
            },
        )
    } else if (isDialogVisible) {
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
                onClick = { onClick(item) },
            )
        }
    }
}

@Composable
private fun <T : PickerPresentation> PickedItemRow(
    state: OptionsMultiPickerState<T>,
    picked: PickedItem<T>,
    itemContent: @Composable (T, onItemRemove: (() -> Unit)?) -> Unit,
) {
    if (!state.isEditable || !state.isQuantityEditable) {
        itemContent(picked.item) { state.onItemsRemoveRequested(listOf(picked.item)) }
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) {
            itemContent(picked.item, null)
        }
        QuantityStepper(
            quantity = picked.quantity,
            onIncrease = { state.onQuantityIncreaseRequested(picked.item) },
            onDecrease = { state.onQuantityDecreaseRequested(picked.item) },
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}
