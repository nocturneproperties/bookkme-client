package me.bookk.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.color
import me.bookk.designsystem.html
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.BooleanState

@Composable
fun StateSwitch(
    state: BooleanState,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedTrackColor = LocalColors.current.actionText,
        checkedThumbColor = Color.White,
        uncheckedThumbColor = Color.White
    ),
    onCheckedChange: (Boolean) -> Unit = { state.onCheckedChange?.invoke(it) }
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
            Switch(
                checked = state.isChecked,
                colors = colors,
                onCheckedChange = onCheckedChange
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

@Preview(showBackground = true)
@Composable
private fun PreviewSwitchButton() {
    StateSwitch(
        state = AndroidBooleanState(
            text = "Example text".desc(),
            isChecked = true
        ),
    )
}