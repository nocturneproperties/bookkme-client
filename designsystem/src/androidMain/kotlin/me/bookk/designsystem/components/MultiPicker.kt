package me.bookk.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.typography.secondary
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
                val placeholder = state.placeholder
                if (state.selectedItems.isEmpty() && placeholder != null) {
                    Text(
                        placeholder.localized(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.secondary()
                    )
                }
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