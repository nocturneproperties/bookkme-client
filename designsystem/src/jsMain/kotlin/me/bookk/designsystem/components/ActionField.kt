package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.primary
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.TextFieldState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val ActionFieldStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .minHeight(3.5.cssRem)
        .padding(left = FIELD_PADDING_REM.cssRem, right = 0.5.cssRem)
        .gap(0.75.cssRem)
        .borderRadius(1.cssRem)
        .backgroundColor(ColorToken.Elevated.resolve(colorMode.webColors()))
}

@Composable
fun ActionField(state: TextFieldState, action: ButtonState, modifier: Modifier = Modifier) {
    if (!state.isVisible) return
    Column(
        modifier
            .fillMaxWidth()
            .gap(0.25.cssRem),
    ) {
        FieldLabel(state.label)
        Row(
            ActionFieldStyle.toModifier(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextBlock(
                state.text.desc(),
                BodyMediumTextStyle
                    .toModifier()
                    .flexGrow(1)
                    .minWidth(0.px)
                    .margin(0.px)
                    .overflowWrap(OverflowWrap.Anywhere)
                    .primary(),
            )
            TextButton(action, Modifier.flexShrink(0))
        }
        SupportingText(state.supportingTextRes, state.validationState)
    }
}
