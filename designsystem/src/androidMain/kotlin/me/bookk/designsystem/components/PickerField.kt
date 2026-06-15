package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.MinimalPickerPresentation
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerFieldState.PickerType
import me.bookk.designsystem.uistate.PickerPresentation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : PickerPresentation> PickerField(
    state: PickerFieldState<T>,
    modifier: Modifier = Modifier,
    screenPicker: @Composable (state: PickerFieldState<T>, onDismiss: () -> Unit, onItemPicked: (T) -> Unit) -> Unit = { _, _, _ -> },
    onItemPicked: (T) -> Unit = { state.onItemPicked(it) }
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    when (state.pickerType) {
        PickerType.BOTTOM_SHEET -> {
            TextField(
                modifier = modifier,
                interactionSource = singleClickInteractionSource {
                    if (state.options.isNotEmpty()) {
                        isSheetVisible = true
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        tint = LocalColors.current.secondaryText,
                        contentDescription = null
                    )
                },
                state = state.textField
            )
            if (isSheetVisible) {
                PickerBottomSheet(state, onItemPicked) {
                    isSheetVisible = false
                }
            }
        }

        PickerType.SCREEN -> {
            TextField(
                modifier = modifier,
                interactionSource = singleClickInteractionSource {
                    if (state.options.isNotEmpty()) {
                        isSheetVisible = true
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = LocalColors.current.secondaryText,
                        contentDescription = null
                    )
                },
                state = state.textField
            )
            if (isSheetVisible) {
                screenPicker(
                    state,
                    {
                        isSheetVisible = false
                    },
                    {
                        state.onItemPicked(it)
                        isSheetVisible = false
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T : PickerPresentation> PickerBottomSheet(
    state: PickerFieldState<T>,
    onItemPicked: (T) -> Unit,
    dismissAction: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    SelectorBottomSheet(
        sheetState = sheetState,
        title = state.pickerTitle,
        preselectItem = state.selectedItem,
        data = state.options,
        onItemPicked = {
            scope.launch {
                sheetState.hide()
                onItemPicked(it)
                dismissAction()
            }
        },
        onDismiss = {
            scope.launch {
                sheetState.hide()
                dismissAction()
            }
        },
        requireConfirmation = false
    ) { _, item, isSelected, onClick ->
        StandardSelectorItem(
            item = item,
            isSelected = isSelected,
            stringify = { it.displayName.localized() },
            onClick = { onClick(item) }
        )
    }
}

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        PickerField(
            AndroidPickerFieldState(
                items = listOf(
                    MinimalPickerPresentation(
                        "1",
                        "Test1".desc()
                    ),
                    MinimalPickerPresentation(
                        "2",
                        "Test2".desc()
                    )
                ),
                selectedItem = null
            ).apply {
                textField.placeholder = "Example hint".desc()
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = WHITE.toLong())
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        AppTheme(themeMode = ThemeMode.LIGHT) {
            PickerField(
                AndroidPickerFieldState(
                    items = listOf(
                        MinimalPickerPresentation(
                            "1",
                            "Test1".desc()
                        ),
                        MinimalPickerPresentation(
                            "2",
                            "Test2".desc()
                        )
                    ),
                    selectedItem = MinimalPickerPresentation(
                        "1",
                        "Test1".desc()
                    )
                ).apply {
                    textField.placeholder = "Example hint".desc()
                    textField.text = "Test1"
                }
            )
        }
    }
}
