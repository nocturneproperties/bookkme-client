package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.Container
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.color
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.ValidationState

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = { state.onTextChanged?.invoke(it) },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    leadingIcon: (@Composable () -> Unit)? = state.startIcon?.let {
        { Icon(painterResource(it), contentDescription = null) }
    },
    trailingIcon: (@Composable () -> Unit)? = state.endIcon?.let {
        { Icon(painterResource(it), contentDescription = null) }
    }
) {
    val colors = defaultTextFieldColors(state.validationState)
    var isFocused by remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = state.text,
            modifier =
                modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        isFocused = it.isFocused
                    }
                    .animateContentSize(),
            onValueChange = onValueChange,
            enabled = state.enabled,
            readOnly = state.readOnly,
            textStyle = textStyle,
            cursorBrush = SolidColor(if (state.validationState.isAtLeastWarning()) state.validationState.color else colors.cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions.copy(
                keyboardType = state.inputType.nativeInputType()
            ),
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox =
                @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = state.text,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        label = state.label.localized().takeIf { it.isNotBlank() }?.let {
                            {
                                val animatedTextSize by animateFloatAsState(
                                    targetValue = if (isFocused || state.text.isNotEmpty()) 11.sp.value else 15.sp.value,
                                    label = "fontSizeAnimation"
                                )
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = animatedTextSize.sp
                                    )
                                )
                            }
                        },
                        placeholder = state.placeholder.localized().takeIf { it.isNotBlank() }
                            ?.let {
                                {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium.secondary()
                                    )
                                }
                            },
                        supportingText = state.supportingTextRes?.let { supportingTextRes ->
                            {
                                Text(
                                    text = supportingTextRes.localized(),
                                    color = state.validationState.color,
                                    style = MaterialTheme.typography.bodySmall.primary(),
                                )
                            }
                        },
                        trailingIcon = trailingIcon,
                        leadingIcon = leadingIcon,
                        shape = MaterialTheme.shapes.large,
                        singleLine = singleLine,
                        enabled = state.enabled,
                        isError = state.validationState.isAtLeastWarning(),
                        interactionSource = interactionSource,
                        colors = colors,
                        container = {
                            TextFieldContainer(
                                isFocused,
                                state,
                                colors,
                                interactionSource
                            )
                        }
                    )
                },
        )
    }
}

fun InputType.nativeInputType(): KeyboardType = when (this) {
    InputType.TEXT -> KeyboardType.Text
    InputType.DIGIT -> KeyboardType.Number
    InputType.DECIMAL -> KeyboardType.Decimal
    InputType.PHONE -> KeyboardType.Phone
    InputType.EMAIL -> KeyboardType.Email
    InputType.ASCII -> KeyboardType.Text
    InputType.PASSWORD -> KeyboardType.Password
}

@Composable
fun SecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit = { state.onTextChanged?.invoke(it) },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.primary(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val colors = defaultTextFieldColors(state.validationState)
        val textState = rememberTextFieldState()
        var isFocused by remember { mutableStateOf(false) }
        var isPasswordVisible by remember { mutableStateOf(false) }
        val obfuscation =
            if (isPasswordVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
        LaunchedEffect(textState.text) {
            onValueChange(textState.text.toString())
        }
        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
            BasicSecureTextField(
                state = textState,
                modifier =
                    modifier
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .animateContentSize(),
                enabled = state.enabled,
                textStyle = textStyle,
                cursorBrush = SolidColor(if (state.validationState.isAtLeastWarning()) colors.errorCursorColor else colors.cursorColor),
                keyboardOptions = keyboardOptions,
                interactionSource = interactionSource,
                textObfuscationMode = obfuscation,
                decorator = TextFieldDefaults.decorator(
                    state = textState,
                    enabled = state.enabled,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    interactionSource = interactionSource,
                    labelPosition = TextFieldLabelPosition.Attached(),
                    label = state.label.localized().takeIf { it.isNotBlank() }?.let {
                        {
                            val animatedTextSize by animateFloatAsState(
                                targetValue = if (isFocused || state.text.isNotEmpty()) 11.sp.value else 15.sp.value,
                                label = "fontSizeAnimation"
                            )
                            Text(
                                text = it,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = animatedTextSize.sp
                                )
                            )
                        }
                    },
                    placeholder = state.placeholder.localized().takeIf { it.isNotBlank() }
                        ?.let {
                            {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium.secondary()
                                )
                            }
                        },
                    supportingText = state.supportingTextRes?.let { supportingTextRes ->
                        {
                            Text(
                                text = supportingTextRes.localized(),
                                color = state.validationState.color,
                                style = MaterialTheme.typography.bodySmall.primary(),
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            val description =
                                if (isPasswordVisible) "Hide password" else "Show password"
                            Crossfade(isPasswordVisible) {
                                val icon =
                                    if (it) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                Image(
                                    imageVector = icon,
                                    contentDescription = description,
                                    colorFilter = ColorFilter.tint(LocalColors.current.secondaryText)
                                )
                            }
                        }
                    },
                    leadingIcon = state.startIcon?.let {
                        { Icon(painterResource(it), contentDescription = null) }
                    },
                    isError = state.validationState.isAtLeastWarning(),
                    colors = colors,
                    outputTransformation = null,
                    container = { TextFieldContainer(isFocused, state, colors, interactionSource) },
                ),
            )
        }
    }
}

@Composable
private fun TextFieldContainer(
    isFocused: Boolean,
    state: TextFieldState,
    colors: TextFieldColors,
    interactionSource: MutableInteractionSource
) {
    val strokeWidth by animateDpAsState(if (isFocused) 2.dp else 1.dp)
    val strokeColor by animateColorAsState(
        when {
            state.validationState.isAtLeastWarning() -> state.validationState.color
            isFocused -> LocalColors.current.actionText
            else -> LocalColors.current.divider
        }
    )
    Container(
        modifier = Modifier
            .height(56.dp)
            .border(
                strokeWidth,
                strokeColor,
                shape = MaterialTheme.shapes.large
            ),
        enabled = state.enabled,
        isError = state.validationState.isAtLeastWarning(),
        colors = colors,
        interactionSource = interactionSource,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
internal fun defaultTextFieldColors(state: ValidationState): TextFieldColors {
    return TextFieldDefaults.colors(
        selectionColors = TextSelectionColors(
            handleColor = LocalColors.current.actionText,
            backgroundColor = LocalColors.current.actionText.copy(alpha = 0.2f),
        ),
        errorLabelColor = state.color,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        cursorColor = LocalColors.current.actionText,
        errorCursorColor = state.color,
        disabledTextColor = LocalColors.current.elevated,
        errorTextColor = state.color,
        disabledContainerColor = LocalColors.current.elevated,
        errorContainerColor = LocalColors.current.elevated,
        focusedContainerColor = LocalColors.current.elevated,
        unfocusedContainerColor = LocalColors.current.elevated,
        errorPlaceholderColor = state.color,
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
                    label = "Type here...".desc(),
                    text = "",
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
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
                    label = "Type here...".desc(),
                    text = "",
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false
                ),
                onValueChange = {}
            )
        }
    }
}