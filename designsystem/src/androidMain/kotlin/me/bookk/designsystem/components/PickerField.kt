package me.bookk.designsystem.components

import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.MinimalPickerPresentation
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerFieldState.PickerType
import me.bookk.designsystem.uistate.PickerPresentation

const val PICKER_RESULT = "pick_result"

@Composable
fun ObservePickerResult(id: String, navController: NavController, onPick: (KeyValueData) -> Unit) {
    val entry = navController.currentBackStackEntry ?: return
    val result by entry.savedStateHandle.getStateFlow<String?>(PICKER_RESULT + id, null)
        .collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        result?.let { onPick(Json.decodeFromString<KeyValueData>(it)) }
    }
}

fun PickerPresentation.toKeyValue(context: Context) = KeyValueData(pickerItemId, displayName.toString(context))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : PickerPresentation> PickerField(
    state: PickerFieldState<T>,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    navigateToScreenPicker: (options: List<T>) -> Unit = {},
    onItemPicked: (T) -> Unit = { state.onItemPicked(it) }
) {
    when (state.pickerType) {
        PickerType.BOTTOM_SHEET -> {
            var isSheetVisible by remember { mutableStateOf(false) }
            TextField(
                modifier = modifier,
                interactionSource = singleClickInteractionSource {
                    if (state.options.isNotEmpty()) {
                        isSheetVisible = true
                    }
                },
                trailingIcon = {
                    state.textField.endIcon?.let {
                        Icon(
                            painter = painterResource(it),
                            tint = LocalColors.current.secondaryText,
                            contentDescription = null
                        )
                    }
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
            val navController =
                requireNotNull(navController) { "Nav controller is required for PickerType.SCREEN" }

            ObservePickerResult(state.id, navController) { result ->
                state.options.firstOrNull { it.pickerItemId == result.key }?.let {
                    onItemPicked(it)
                }
            }

            TextField(
                modifier = modifier,
                interactionSource = singleClickInteractionSource {
                    navigateToScreenPicker(state.options)
                },
                trailingIcon = {
                    state.textField.endIcon?.let {
                        Icon(
                            painter = painterResource(it),
                            tint = LocalColors.current.secondaryText,
                            contentDescription = null
                        )
                    }
                },
                state = state.textField
            )
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
