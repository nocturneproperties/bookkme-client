package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.themed
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.attributes.InputType as HtmlInputType

@Composable
fun TextField(state: TextFieldState, modifier: Modifier = Modifier) {
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
                .secondary(),
        )
        Input(
            type = state.inputType.html(),
            value = state.text,
            onValueChange = { state.onTextChanged?.invoke(it.take(state.maxLength)) },
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(ColorToken.Elevated.themed)
                .attrsModifier {
                    if (state.maxLength !=
                        Int.MAX_VALUE
                    ) {
                        attr("maxlength", "${state.maxLength}")
                    }
                },
            placeholder = state.placeholder.localized(),
            enabled = state.enabled,
            valid = state.validationState != ValidationState.ERROR,
            readOnly = state.readOnly,
            focusBorderColor = ColorToken.ActionText.themed,
            invalidBorderColor = ColorToken.Error.themed,
        )
        SupportingText(state.supportingTextRes, state.validationState)
    }
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
