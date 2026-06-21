package me.bookk.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation

@Composable
fun <T : PickerPresentation> MultiPicker(
    state: MultiPickerState<T>,
    modifier: Modifier = Modifier,
    pickerContent: @Composable () -> Unit,
    itemContent: @Composable (T, onItemRemove: () -> Unit) -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        Header(state.pickerTitle.localized())
        AppCard(Modifier.fillMaxWidth()) {
            Column(Modifier.animateContentSize()) {
                state.selectedItems.forEach {
                    itemContent(it) {
                        state.onItemsRemoveRequested(listOf(it))
                    }
                }
                if (state.isEditable) {
                    AlignStartTextButton(
                        state.addItemButton,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.isPickerVisible = true
                    }
                }
            }
        }
    }
    if (state.isPickerVisible) {
        pickerContent()
    }
}