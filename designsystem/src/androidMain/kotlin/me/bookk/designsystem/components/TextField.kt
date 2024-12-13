package me.bookk.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.string
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.text.LocalAppTypography
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TextFieldStateImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = defaultTextFieldColors(),
    textStyle: TextStyle = LocalAppTypography.current.body1Regular,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minHeight: Dp = 24.dp,
    decorationBoxContentPaddingValues: PaddingValues = PaddingValues(vertical = 4.dp),
) {
    Column(modifier = modifier) {
        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
            BasicTextField(
                value = state.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(
                        minWidth = TextFieldDefaults.MinWidth,
                        minHeight = minHeight
                    ),
                onValueChange = onValueChange,
                enabled = state.enabled,
                readOnly = state.readOnly,
                textStyle = textStyle,
                cursorBrush = SolidColor(colors.cursorColor),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                decorationBox = @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = state.text,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = {
                            Text(
                                text = state.hint.string(),
                                style = LocalAppTypography.current.body1Regular,
                                textAlign = TextAlign.Start
                            )
                        },
                        singleLine = singleLine,
                        enabled = state.enabled,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = decorationBoxContentPaddingValues
                    )
                }
            )
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
internal fun defaultTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        selectionColors = TextSelectionColors(
            handleColor = LocalColors.current.ActionText,
            backgroundColor = LocalColors.current.ActionText.copy(alpha = 0.2f),
        ),
        cursorColor = LocalColors.current.ActionText,
        errorCursorColor = LocalColors.current.ActionText,
        focusedPlaceholderColor = LocalColors.current.ActionText,
        unfocusedPlaceholderColor = LocalColors.current.ActionText,
        disabledPlaceholderColor = LocalColors.current.ActionTextDisabled,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
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
            SimpleTextField(
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
            SimpleTextField(
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
            SimpleTextField(
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
            SimpleTextField(
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