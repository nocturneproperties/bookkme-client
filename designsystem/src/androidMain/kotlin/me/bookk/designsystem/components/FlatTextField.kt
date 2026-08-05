package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.TextFieldState

@Composable
fun FlatTextField(
    state: TextFieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = state.inputType.nativeInputType()),
    focusRequester: FocusRequester? = null
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        state.startIcon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                tint = LocalColors.current.secondaryText,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
        }
        BasicTextField(
            value = state.text,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .let { base -> focusRequester?.let { base.focusRequester(it) } ?: base },
            enabled = state.enabled,
            readOnly = state.readOnly,
            singleLine = singleLine,
            textStyle = textStyle.copy(color = LocalColors.current.primaryText),
            cursorBrush = SolidColor(LocalColors.current.actionText),
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Box {
                    if (state.text.isEmpty()) {
                        Text(
                            text = state.placeholder.localized(),
                            style = textStyle,
                            color = LocalColors.current.hintText
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme(themeMode = ThemeMode.DARK) {
        Box(Modifier.padding(16.dp)) {
            FlatTextField(
                state = AndroidTextFieldState(placeholder = "Placeholder".desc(), text = ""),
                onValueChange = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        Box(Modifier.padding(16.dp)) {
            FlatTextField(
                state = AndroidTextFieldState(placeholder = "Placeholder".desc(), text = "Value"),
                onValueChange = {}
            )
        }
    }
}
