package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.RadioButtonState
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Composable
fun RadioButton(state: RadioButtonState, modifier: Modifier = Modifier) {
    if (!state.isVisible) return
    val clickable = if (state.isEnabled) {
        Modifier
            .cursor(Cursor.Pointer)
            .onClick { state.onClick?.invoke() }
    } else {
        Modifier.opacity(0.5)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .minHeight(3.cssRem)
            .then(clickable)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextBlock(state.text, BodyLargeTextStyle.toModifier())
        Spacer()
        RadioIndicator(state.isSelected)
    }
}

@Composable
private fun RadioIndicator(isSelected: Boolean) {
    val color = if (isSelected) ColorToken.ActionText.themed else ColorToken.Divider.themed
    Box(
        Modifier
            .size(1.25.cssRem)
            .borderRadius(50.percent)
            .border(2.px, LineStyle.Solid, color),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                Modifier
                    .size(0.625.cssRem)
                    .borderRadius(50.percent)
                    .backgroundColor(color),
            )
        }
    }
}
