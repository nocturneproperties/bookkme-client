package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun CheckBox(
    modifier: Modifier,
    checked: Boolean,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = LocalColors.current.primaryText.copy(alpha = 0.2f),
        uncheckedColor = LocalColors.current.primaryText,
        disabledCheckedColor = LocalColors.current.inactive,
        disabledUncheckedColor = LocalColors.current.inactive,
    ),
    onCheckedChange: (Boolean) -> Unit
) {
    Checkbox(
        modifier = modifier,
        checked = checked,
        colors = colors,
        onCheckedChange = onCheckedChange
    )
}


@Preview
@Composable
private fun PreviewCheckBox() {
    AppTheme {
        Column {
            CheckBox(modifier = Modifier, checked = true, onCheckedChange = {})
            CheckBox(modifier = Modifier, checked = false, onCheckedChange = {})
        }
    }
}