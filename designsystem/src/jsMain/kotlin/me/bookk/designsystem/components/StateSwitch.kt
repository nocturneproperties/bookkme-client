package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.silk.components.forms.Checkbox
import com.varabyte.kobweb.silk.components.forms.CheckboxSize
import com.varabyte.kobweb.silk.components.forms.CheckboxVars
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchVars
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ValidationState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Text

@Composable
fun StateSwitch(state: BooleanState, modifier: Modifier = Modifier) {
    if (!state.isVisible) return
    Column(modifier.gap(0.25.cssRem)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextBlock(state.text, BodyMediumTextStyle.toModifier())
            Spacer()
            val track = if (state.isChecked) ColorToken.Success else ColorToken.InactiveToggle
            Switch(
                checked = state.isChecked,
                onCheckedChange = { state.onCheckedChange?.invoke(it) },
                modifier = Modifier.setVariable(SwitchVars.TrackBackgroundColor, track.themed),
                enabled = state.isEnabled,
            )
        }
        SupportingText(state.supportingTextRes, state.validationState)
    }
}

val CheckBoxStyle = CssStyle {
    cssRule(" > div") { Modifier.flexShrink(0) }
}

@Composable
fun CheckBox(state: BooleanState, modifier: Modifier = Modifier) {
    if (!state.isVisible) return
    Column(modifier.gap(0.25.cssRem)) {
        Checkbox(
            checked = state.isChecked,
            onCheckedChange = { state.onCheckedChange?.invoke(it) },
            modifier = checkBoxModifier(),
            enabled = state.isEnabled,
            size = CheckboxSize.LG,
            borderColor = checkBoxBorder(state.validationState).themed,
            uncheckedColor = Colors.Transparent,
            iconColor = ColorToken.OnAction.themed,
        ) {
            Text(state.text.localized())
        }
        SupportingText(state.supportingTextRes, state.validationState)
    }
}

@Composable
private fun checkBoxModifier(): Modifier {
    val checkedColor = ColorToken.ButtonActive.themed
    return CheckBoxStyle
        .toModifier()
        .setVariable(CheckboxVars.Spacing, 0.75.cssRem)
        .setVariable(CheckboxVars.FontSize, 1.cssRem)
        .setVariable(CheckboxVars.BorderRadius, 0.375.cssRem)
        .setVariable(CheckboxVars.IconBackgroundColor, checkedColor)
        .setVariable(CheckboxVars.IconBackgroundHoverColor, checkedColor)
}

private fun checkBoxBorder(validationState: ValidationState): ColorToken {
    return if (validationState ==
        ValidationState.ERROR
    ) {
        ColorToken.Error
    } else {
        ColorToken.SecondaryText
    }
}
