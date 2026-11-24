package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.forms.FilledInputVariant
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.silk.components.forms.InputStyle
import com.varabyte.kobweb.silk.components.forms.InputVars
import com.varabyte.kobweb.silk.style.common.PlaceholderColor
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.ValidationState
import org.jetbrains.compose.web.attributes.builders.TextAreaAttrsScope
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.attributes.rows
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.attributes.InputType as HtmlInputType

private const val TEXT_AREA_LINE_HEIGHT = 1.5
private const val FIELD_PADDING_REM = 1.0
private const val LABEL_FONT_SIZE_REM = 0.9375

@Composable
fun TextField(state: TextFieldState, modifier: Modifier = Modifier, minLines: Int = 1) {
    if (!state.isVisible) return
    Column(
        modifier
            .fillMaxWidth()
            .gap(0.25.cssRem),
    ) {
        TextBlock(
            state.label,
            BodySmallTextStyle
                .toModifier()
                .fontSize(LABEL_FONT_SIZE_REM.cssRem)
                .fontWeight(FontWeight.Bold)
                .secondary(),
        )
        when (minLines) {
            1 -> SingleLineInput(state)
            else -> MultiLineInput(state, minLines)
        }
        SupportingText(state.supportingTextRes, state.validationState)
    }
}

@Composable
private fun SingleLineInput(state: TextFieldState) {
    Input(
        type = state.inputType.html(),
        value = state.text,
        onValueChange = { state.onTextChanged?.invoke(it.take(state.maxLength)) },
        modifier = fieldModifier(state).setVariable(InputVars.Height, 3.5.cssRem),
        variant = FilledInputVariant,
        placeholder = state.placeholder.localized(),
        placeholderColor = PlaceholderColor(ColorToken.HintText.themed),
        enabled = state.enabled,
        valid = state.isValidInput(),
        readOnly = state.readOnly,
        focusBorderColor = ColorToken.ActionText.themed,
        invalidBorderColor = ColorToken.Error.themed,
    )
}

@Composable
private fun MultiLineInput(state: TextFieldState, minLines: Int) {
    val hint = state.placeholder.localized()
    TextArea(
        value = state.text,
        attrs = InputStyle
            .toModifier(FilledInputVariant)
            .then(fieldModifier(state))
            .then(textAreaModifier(state, minLines))
            .toAttrs<TextAreaAttrsScope> {
                rows(minLines)
                placeholder(hint)
                if (!state.enabled) disabled()
                if (state.readOnly) readOnly()
                onInput { state.onTextChanged?.invoke(it.value.take(state.maxLength)) }
            },
    )
}

@Composable
private fun fieldModifier(state: TextFieldState): Modifier {
    return Modifier
        .fillMaxWidth()
        .setVariable(InputVars.FontSize, 1.cssRem)
        .setVariable(InputVars.Padding, FIELD_PADDING_REM.cssRem)
        .setVariable(InputVars.BorderRadius, 1.cssRem)
        .setVariable(InputVars.FilledColor, ColorToken.Elevated.themed)
        .setVariable(InputVars.FilledHoverColor, ColorToken.Elevated.themed)
        .setVariable(InputVars.FilledFocusColor, ColorToken.Elevated.themed)
        .attrsModifier {
            if (state.maxLength != Int.MAX_VALUE) attr("maxlength", "${state.maxLength}")
        }
}

@Composable
private fun textAreaModifier(state: TextFieldState, minLines: Int): Modifier {
    return Modifier
        .boxSizing(BoxSizing.BorderBox)
        .lineHeight(TEXT_AREA_LINE_HEIGHT)
        .padding(FIELD_PADDING_REM.cssRem)
        .setVariable(InputVars.PlaceholderColor, ColorToken.HintText.themed)
        .setVariable(InputVars.BorderFocusColor, ColorToken.ActionText.themed)
        .setVariable(InputVars.BorderInvalidColor, ColorToken.Error.themed)
        .styleModifier {
            property("height", "auto")
            property("min-height", "calc(${minLines}lh + ${2 * FIELD_PADDING_REM}rem + 2px)")
            property("field-sizing", "content")
            property("resize", "none")
            property("font-family", "inherit")
        }
        .attrsModifier {
            if (!state.isValidInput()) attr("aria-invalid", "true")
        }
}

private fun TextFieldState.isValidInput(): Boolean {
    return validationState != ValidationState.ERROR
}

private fun InputType.html(): HtmlInputType<String> {
    return when (this) {
        InputType.TEXT, InputType.ASCII -> HtmlInputType.Text
        InputType.DIGIT, InputType.DECIMAL -> HtmlInputType.Text
        InputType.PHONE -> HtmlInputType.Tel
        InputType.EMAIL -> HtmlInputType.Email
        InputType.PASSWORD -> HtmlInputType.Password
    }
}
