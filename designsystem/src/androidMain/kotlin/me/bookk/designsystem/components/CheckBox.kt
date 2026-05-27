package me.bookk.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.color
import me.bookk.designsystem.html
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.uistate.CheckBoxState


@Composable
fun CheckBox(
    state: CheckBoxState,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = LocalColors.current.primaryText,
        uncheckedColor = LocalColors.current.primaryText,
        disabledCheckedColor = LocalColors.current.inactive,
        disabledUncheckedColor = LocalColors.current.inactive,
    ),
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    state.onCheckedChange?.invoke(!state.isChecked)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Checkbox(
                checked = state.isChecked,
                colors = colors,
                onCheckedChange = state.onCheckedChange
            )
            Text(
                text = state.text.html(),
                style = MaterialTheme.typography.bodyLarge.primary()
            )
        }
        AnimatedVisibility(state.supportingTextRes != null) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = state.supportingTextRes?.localized().orEmpty(),
                color = state.validationState.color,
                style = MaterialTheme.typography.labelSmall.primary(),
            )
        }
    }
}

@Composable
fun CheckBoxSelector(
    state: CheckBoxState,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = LocalColors.current.primaryText,
        uncheckedColor = LocalColors.current.primaryText,
        disabledCheckedColor = LocalColors.current.inactive,
        disabledUncheckedColor = LocalColors.current.inactive,
    ),
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    state.onCheckedChange?.invoke(!state.isChecked)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = state.text.html(),
                style = MaterialTheme.typography.bodyLarge.primary()
            )
            if (state.isChecked) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = LocalColors.current.actionText
                )
            }
        }
        AnimatedVisibility(state.supportingTextRes != null) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = state.supportingTextRes?.localized().orEmpty(),
                color = state.validationState.color,
                style = MaterialTheme.typography.labelSmall.primary(),
            )
        }
    }
}

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