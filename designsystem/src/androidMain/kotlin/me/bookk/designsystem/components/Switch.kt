package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidSwitchState
import me.bookk.designsystem.uistate.SwitchState

@Composable
fun StateSwitch(
    modifier: Modifier = Modifier,
    state: SwitchState,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            text = state.text.localized()
        )
        Switch(
            checked = state.isChecked,
            colors = SwitchDefaults.colors(
                checkedTrackColor = LocalColors.current.ActionText
            ),
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSwitchButton() {
    StateSwitch(
        state = AndroidSwitchState(
            text = "Example text".desc(),
            isChecked = true
        ),
        onCheckedChange = {}
    )
}