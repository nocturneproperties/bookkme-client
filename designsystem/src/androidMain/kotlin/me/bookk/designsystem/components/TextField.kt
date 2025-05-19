package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.TextFieldState

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        value = state.text,
        onValueChange = onValueChange,
        enabled = state.enabled,
        readOnly = state.readOnly,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        colors = defaultTextFieldColors(isError = state.isError),
        placeholder = {
            Text(
                text = state.hint.localized(),
                style = MaterialTheme.typography.bodyMedium.copy(background = Color.Transparent)
            )
        },
        supportingText = {
            state.supportingTextRes?.let { supportingTextRes ->
                Text(
                    text = supportingTextRes.localized(),
                    color = if (state.isError) LocalColors.current.error else LocalColors.current.secondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    )
}

@Composable
internal fun defaultTextFieldColors(isError: Boolean): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        selectionColors = TextSelectionColors(
            handleColor = LocalColors.current.actionText,
            backgroundColor = LocalColors.current.actionText.copy(alpha = 0.2f),
        ),
        cursorColor = LocalColors.current.actionText,
        errorCursorColor = LocalColors.current.error,
        disabledTextColor = LocalColors.current.actionTextDisabled,
        errorTextColor = LocalColors.current.primaryText,
        disabledContainerColor = LocalColors.current.elevated,
        errorContainerColor = LocalColors.current.elevated,
        focusedContainerColor = LocalColors.current.elevated,
        unfocusedContainerColor = LocalColors.current.elevated,
        disabledBorderColor = if (isError) LocalColors.current.error else Color.Transparent,
        focusedBorderColor = if (isError) LocalColors.current.error else LocalColors.current.actionText,
        errorBorderColor = LocalColors.current.error,
        unfocusedBorderColor = if (isError) LocalColors.current.error else Color.Transparent,
        errorPlaceholderColor = LocalColors.current.error,
        disabledPlaceholderColor = LocalColors.current.actionTextDisabled,
        focusedPlaceholderColor = LocalColors.current.hintText,
        unfocusedPlaceholderColor = LocalColors.current.hintText
    )
}

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun Preview() {
    AppTheme(themeMode = ThemeMode.DARK) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = WHITE.toLong())
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    hint = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
        }
    }
}