package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.MinimalPickerPresentation
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : PickerPresentation> PickerField(
    state: PickerFieldState<T>,
    modifier: Modifier = Modifier,
    onItemPicked: (T) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
        }
    ) {
        val text = state.selectedItem.displayName.localized()
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            state = remember(key1 = text) {
                AndroidTextFieldState(
                    text = text,
                    readOnly = true
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            onValueChange = {}
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            containerColor = LocalColors.current.elevated,
            shape = MaterialTheme.shapes.large,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            state.options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.displayName.localized(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onItemPicked(state.options[index])
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
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
                        0,
                        "Test1".desc()
                    ),
                    MinimalPickerPresentation(
                        1,
                        "Test2".desc()
                    )
                ),
                selectedItem = MinimalPickerPresentation(
                    0,
                    "Test1".desc()
                )
            )
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
                            0,
                            "Test1".desc()
                        ),
                        MinimalPickerPresentation(
                            1,
                            "Test2".desc()
                        )
                    ),
                    selectedItem = MinimalPickerPresentation(
                        0,
                        "Test1".desc()
                    )
                )
            )
        }
    }
}
