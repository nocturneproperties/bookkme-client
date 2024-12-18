package me.bookk.designsystem.components

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.bookk.core.presentation.string
import me.bookk.designsystem.theme.text.LocalAppTypography
import me.bookk.designsystem.uistate.TextFieldState

@Composable
fun AppOutlinedTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = defaultOutlinedTextFieldColors(),
    textStyle: TextStyle = LocalAppTypography.current.body1Regular,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    minHeight: Dp = 48.dp,
    maxHeight: Dp? = null,
) {
    AppOutlinedTextField(
        modifier = modifier,
        value = state.text,
        enabled = state.enabled,
        readOnly = state.readOnly,
        visualTransformation = visualTransformation,
        isError = state.isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = {
            Text(
                color = colors.placeholderColor(
                    enabled = state.enabled,
                    isError = state.isError,
                    interactionSource = interactionSource
                ).value,
                text = state.hint.string(),
                style = textStyle,
            )
        },
        onValueChange = { onValueChange(it.take(state.maxLength)) },
        colors = colors,
        textStyle = textStyle,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        minHeight = minHeight,
        maxHeight = maxHeight
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ShapeDefaults.Medium,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    minHeight: Dp = 48.dp,
    maxHeight: Dp? = null,
    decorationBoxContentPaddingValues: PaddingValues = PaddingValues(12.dp),
    focusedBorderThickness: Dp = OutlinedTextFieldDefaults.UnfocusedBorderThickness,
    unfocusedBorderThickness: Dp = OutlinedTextFieldDefaults.UnfocusedBorderThickness,
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource
        ).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val cursorColor = if (isError) colors.errorCursorColor else colors.cursorColor

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            modifier = modifier
                .semantics(mergeDescendants = true) {}
                .defaultMinSize(
                    minWidth = OutlinedTextFieldDefaults.MinWidth,
                    minHeight = minHeight
                )
                .then(if (maxHeight != null) Modifier.sizeIn(maxHeight = maxHeight) else Modifier),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    container = {
                        Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = shape,
                            focusedBorderThickness = focusedBorderThickness,
                            unfocusedBorderThickness = unfocusedBorderThickness,
                        )
                    },
                    contentPadding = decorationBoxContentPaddingValues
                )
            }
        )
    }
}

@Composable
internal fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

@Composable
internal fun TextFieldColors.placeholderColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledPlaceholderColor
        isError -> errorPlaceholderColor
        focused -> focusedPlaceholderColor
        else -> unfocusedPlaceholderColor
    }
    return rememberUpdatedState(targetValue)
}
