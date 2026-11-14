package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.ValidationState

private val FocusedBorderWidth = 2.dp
private val UnfocusedBorderWidth = 1.dp
private val FallbackLabelLineHeight = 16.sp

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = { state.onTextChanged?.invoke(it) },
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.primary(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    leadingIcon: (@Composable () -> Unit)? = state.startIcon?.let {
        { Icon(painterResource(it), contentDescription = null) }
    },
    trailingIcon: (@Composable () -> Unit)? = state.endIcon?.let {
        { Icon(painterResource(it), contentDescription = null) }
    },
) {
    val colors = defaultTextFieldColors(state.validationState)
    val isError = state.validationState.isAtLeastWarning()
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = state.text,
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = floatingLabelTopSpace(state)),
            onValueChange = onValueChange,
            enabled = state.enabled,
            readOnly = state.readOnly,
            textStyle = textStyle,
            cursorBrush = SolidColor(if (isError) colors.errorCursorColor else colors.cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions.copy(
                keyboardType = state.inputType.nativeInputType(),
            ),
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                val containerHeight = remember { mutableIntStateOf(0) }
                ContainerFill(state, colors, interactionSource, containerHeight) {
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = state.text,
                        innerTextField = innerTextField,
                        enabled = state.enabled,
                        singleLine = singleLine,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        isError = isError,
                        label = fieldLabel(state),
                        placeholder = fieldPlaceholder(state),
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon ?: errorIcon(isError),
                        suffix = state.suffix?.let { { Text(it.localized()) } },
                        supportingText = fieldSupportingText(state),
                        colors = colors,
                        container = {
                            FieldContainer(state, colors, interactionSource, containerHeight)
                        },
                    )
                }
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        val colors = defaultTextFieldColors(state.validationState)
        val isError = state.validationState.isAtLeastWarning()
        val textState = rememberTextFieldState()
        var isPasswordVisible by remember { mutableStateOf(false) }
        val obfuscation = if (isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        }
        LaunchedEffect(textState.text) {
            onValueChange(textState.text.toString())
        }
        val containerHeight = remember { mutableIntStateOf(0) }
        val outlinedDecorator = OutlinedTextFieldDefaults.decorator(
            state = textState,
            enabled = state.enabled,
            lineLimits = TextFieldLineLimits.SingleLine,
            outputTransformation = null,
            interactionSource = interactionSource,
            labelPosition = TextFieldLabelPosition.Attached(),
            label = state.label
                .localized()
                .takeIf { it.isNotBlank() }
                ?.let { { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) } },
            placeholder = fieldPlaceholder(state),
            supportingText = fieldSupportingText(state),
            trailingIcon = {
                PasswordVisibilityToggle(isPasswordVisible) {
                    isPasswordVisible = !isPasswordVisible
                }
            },
            leadingIcon = state.startIcon?.let {
                { Icon(painterResource(it), contentDescription = null) }
            },
            isError = isError,
            colors = colors,
            container = { FieldContainer(state, colors, interactionSource, containerHeight) },
        )
        CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
            BasicSecureTextField(
                state = textState,
                modifier = modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(top = floatingLabelTopSpace(state)),
                enabled = state.enabled,
                textStyle = textStyle,
                cursorBrush = SolidColor(
                    if (isError) colors.errorCursorColor else colors.cursorColor,
                ),
                keyboardOptions = keyboardOptions,
                interactionSource = interactionSource,
                textObfuscationMode = obfuscation,
                decorator = TextFieldDecorator { innerTextField ->
                    ContainerFill(state, colors, interactionSource, containerHeight) {
                        outlinedDecorator.Decoration(innerTextField)
                    }
                },
            )
        }
    }
}

@Composable
private fun PasswordVisibilityToggle(isPasswordVisible: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        val description = if (isPasswordVisible) "Hide password" else "Show password"
        Crossfade(isPasswordVisible) {
            val icon = if (it) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            Image(
                imageVector = icon,
                contentDescription = description,
                colorFilter = ColorFilter.tint(LocalColors.current.secondaryText),
            )
        }
    }
}

@Composable
private fun ContainerFill(
    state: TextFieldState,
    colors: TextFieldColors,
    interactionSource: InteractionSource,
    containerHeight: IntState,
    content: @Composable () -> Unit,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isError = state.validationState.isAtLeastWarning()
    val fillColor = colors.currentContainerColor(state.enabled, isError, isFocused)
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = Modifier.drawBehind {
            val fillSize = Size(size.width, containerHeight.intValue.toFloat())
            drawOutline(shape.createOutline(fillSize, layoutDirection, this), fillColor)
        },
        propagateMinConstraints = true,
    ) {
        content()
    }
}

private fun TextFieldColors.currentContainerColor(
    enabled: Boolean,
    isError: Boolean,
    isFocused: Boolean,
): Color {
    return when {
        !enabled -> disabledContainerColor
        isError -> errorContainerColor
        isFocused -> focusedContainerColor
        else -> unfocusedContainerColor
    }
}

private fun TextFieldColors.withTransparentContainer(): TextFieldColors {
    return copy(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
    )
}

@Composable
private fun floatingLabelTopSpace(state: TextFieldState): Dp {
    if (state.label.localized().isBlank()) return 0.dp
    val lineHeight = MaterialTheme.typography.bodySmall.lineHeight
    val labelLineHeight = if (lineHeight.isSp) lineHeight else FallbackLabelLineHeight
    return with(LocalDensity.current) { labelLineHeight.toDp() / 2 }
}

@Composable
private fun fieldLabel(state: TextFieldState): (@Composable () -> Unit)? {
    return state.label
        .localized()
        .takeIf { it.isNotBlank() }
        ?.let { { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
}

@Composable
private fun fieldPlaceholder(state: TextFieldState): (@Composable () -> Unit)? {
    return state.placeholder
        .localized()
        .takeIf { it.isNotBlank() }
        ?.let { { Text(it, style = MaterialTheme.typography.bodyLarge) } }
}

@Composable
private fun fieldSupportingText(state: TextFieldState): (@Composable () -> Unit)? {
    return state.supportingTextRes?.let { supportingText ->
        { Text(text = supportingText.localized(), color = state.validationState.color) }
    }
}

private fun errorIcon(isError: Boolean): (@Composable () -> Unit)? {
    if (!isError) return null
    return { Icon(Icons.Filled.Error, contentDescription = null) }
}

@Composable
private fun FieldContainer(
    state: TextFieldState,
    colors: TextFieldColors,
    interactionSource: InteractionSource,
    containerHeight: MutableIntState,
) {
    OutlinedTextFieldDefaults.Container(
        enabled = state.enabled,
        isError = state.validationState.isAtLeastWarning(),
        interactionSource = interactionSource,
        modifier = Modifier.onSizeChanged { containerHeight.intValue = it.height },
        colors = colors.withTransparentContainer(),
        shape = MaterialTheme.shapes.medium,
        focusedBorderThickness = FocusedBorderWidth,
        unfocusedBorderThickness = UnfocusedBorderWidth,
    )
}

@Composable
internal fun defaultTextFieldColors(state: ValidationState): TextFieldColors {
    val colors = LocalColors.current
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.primaryText,
        unfocusedTextColor = colors.primaryText,
        disabledTextColor = colors.secondaryText,
        errorTextColor = colors.primaryText,
        errorContainerColor = LocalColors.current.elevated,
        focusedContainerColor = LocalColors.current.elevated,
        unfocusedContainerColor = LocalColors.current.elevated,
        disabledContainerColor = LocalColors.current.elevated,
        cursorColor = colors.actionText,
        errorCursorColor = state.color,
        selectionColors = TextSelectionColors(
            handleColor = colors.actionText,
            backgroundColor = colors.actionText.copy(alpha = 0.2f),
        ),
        focusedBorderColor = colors.actionText,
        unfocusedBorderColor = colors.divider,
        disabledBorderColor = colors.divider,
        errorBorderColor = state.color,
        focusedLeadingIconColor = colors.secondaryText,
        unfocusedLeadingIconColor = colors.secondaryText,
        disabledLeadingIconColor = colors.actionTextDisabled,
        errorLeadingIconColor = colors.secondaryText,
        focusedTrailingIconColor = colors.secondaryText,
        unfocusedTrailingIconColor = colors.secondaryText,
        disabledTrailingIconColor = colors.actionTextDisabled,
        errorTrailingIconColor = state.color,
        focusedLabelColor = colors.actionText,
        unfocusedLabelColor = colors.secondaryText,
        disabledLabelColor = colors.actionTextDisabled,
        errorLabelColor = state.color,
        focusedPlaceholderColor = colors.hintText,
        unfocusedPlaceholderColor = colors.hintText,
        disabledPlaceholderColor = colors.actionTextDisabled,
        errorPlaceholderColor = colors.hintText,
        focusedSupportingTextColor = colors.secondaryText,
        unfocusedSupportingTextColor = colors.secondaryText,
        disabledSupportingTextColor = colors.actionTextDisabled,
        errorSupportingTextColor = state.color,
        focusedSuffixColor = colors.secondaryText,
        unfocusedSuffixColor = colors.secondaryText,
        disabledSuffixColor = colors.actionTextDisabled,
        errorSuffixColor = colors.secondaryText,
    )
}

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun Preview() {
    AppTheme(themeMode = ThemeMode.DARK) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error".desc(),
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
            TextField(
                state = AndroidTextFieldState(
                    label = "Type here...".desc(),
                    text = "Text",
                    supportingTextRes = "Error description".desc(),
                    validationState = ValidationState.ERROR,
                    enabled = true,
                    readOnly = false,
                ),
                onValueChange = {},
            )
        }
    }
}
