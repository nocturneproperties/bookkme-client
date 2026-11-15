package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.silk.components.forms.Checkbox
import com.varabyte.kobweb.silk.components.forms.CheckboxVars
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchVars
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.themed
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

@Composable
fun CheckBox(state: BooleanState, modifier: Modifier = Modifier) {
    if (!state.isVisible) return
    Column(modifier.gap(0.25.cssRem)) {
        Checkbox(
            checked = state.isChecked,
            onCheckedChange = { state.onCheckedChange?.invoke(it) },
            modifier = Modifier.setVariable(CheckboxVars.Spacing, 0.75.cssRem),
            enabled = state.isEnabled,
            borderColor = ColorToken.Divider.themed,
        ) {
            Text(state.text.localized())
        }
        SupportingText(state.supportingTextRes, state.validationState)
    }
}
