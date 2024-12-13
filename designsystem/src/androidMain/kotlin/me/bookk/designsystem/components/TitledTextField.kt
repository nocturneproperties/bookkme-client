package me.bookk.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.string
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.text.LocalAppTypography
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TextFieldStateImpl

@Composable
fun TitledTextField(
    title: StringDesc,
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = defaultOutlinedTextFieldColors(),
    titleColor: Color = LocalColors.current.Header,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = title.string(),
            style = LocalAppTypography.current.caption1Regular,
            color = titleColor
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppOutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.text,
                enabled = state.enabled,
                readOnly = state.readOnly,
                visualTransformation = visualTransformation,
                trailingIcon = trailingIcon,
                isError = state.isError,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                leadingIcon = leadingIcon,
                placeholder = {
                    Text(
                        color = colors.placeholderColor(
                            enabled = state.enabled,
                            isError = state.isError,
                            interactionSource = interactionSource
                        ).value,
                        text = state.hint.string(),
                        style = LocalAppTypography.current.body1Regular,
                    )
                },
                onValueChange = { onValueChange(it.take(state.maxLength)) },
                colors = colors,
                interactionSource = interactionSource,
                minLines = minLines,
                singleLine = singleLine,
                maxLines = maxLines,
            )
            trailingContent?.let { it() }
        }

        if (state.isError) {
            Text(
                text = state.errorTextRes?.string().orEmpty(),
                color = AppColors.Error,
                style = LocalAppTypography.current.caption1Regular,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
internal fun defaultOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        selectionColors = TextSelectionColors(
            handleColor = LocalColors.current.ActionText,
            backgroundColor = LocalColors.current.ActionText.copy(alpha = 0.2f),
        ),
        cursorColor = LocalColors.current.ActionText,
        errorCursorColor = LocalColors.current.ActionText,
        focusedPlaceholderColor = LocalColors.current.ActionText,
        unfocusedPlaceholderColor = LocalColors.current.ActionText,
        disabledPlaceholderColor = LocalColors.current.ActionTextDisabled,
        errorTextColor = AppColors.Error,
        focusedBorderColor = LocalColors.current.Divider,
        unfocusedBorderColor = LocalColors.current.Divider,
        disabledBorderColor = LocalColors.current.Divider,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitledTextField(
                title = "Hint".desc(),
                state = TextFieldStateImpl(
                    hint = "Type here...".desc(),
                    text = "",
                    errorTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TitledTextField(
                title = "Entered Text".desc(),
                state = TextFieldStateImpl(
                    hint = "Type here...".desc(),
                    text = "Text",
                    errorTextRes = "Error".desc(),
                    isError = false,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TitledTextField(
                title = "Error + Hint".desc(),
                state = TextFieldStateImpl(
                    hint = "Type here...".desc(),
                    text = "",
                    errorTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TitledTextField(
                title = "Error + Entered Text".desc(),
                state = TextFieldStateImpl(
                    hint = "Type here...".desc(),
                    text = "Text",
                    errorTextRes = "Error description".desc(),
                    isError = true,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
        }
    }
}