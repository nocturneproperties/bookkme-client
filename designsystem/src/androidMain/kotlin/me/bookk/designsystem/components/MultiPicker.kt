package me.bookk.designsystem.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.typography.active
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation

@Composable
fun <T : PickerPresentation> MultiPicker(
    state: MultiPickerState<T>,
    modifier: Modifier = Modifier,
    pickerContent: @Composable () -> Unit,
    itemContent: @Composable (T, () -> Unit) -> Unit
) {
    var isPickerVisible by remember { mutableStateOf(false) }
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
                        onClick = { isPickerVisible = true }
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
    if (isPickerVisible) {
        pickerContent()
    }
}