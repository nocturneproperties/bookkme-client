package me.bookk.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.uistate.AndroidRadioButtonState
import me.bookk.designsystem.uistate.RadioButtonState

private val DefaultRadioButtonSize = 20.dp
private val DefaultRadioButtonPadding = 2.dp
private val DefaultRadioStrokeWidth = 1.dp

@Composable
fun RadioButton(
    modifier: Modifier = Modifier,
    state: RadioButtonState,
    prefix: @Composable () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.primary(),
    onClick: (() -> Unit)? = state.onClick,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .selectable(
                selected = state.isSelected,
                onClick = { onClick?.invoke() },
                enabled = state.isEnabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        prefix()
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = state.text.localized(),
            style = textStyle
        )

        CustomRadioButtonDrawable(state)
    }
}

@Composable
private fun CustomRadioButtonDrawable(
    state: RadioButtonState,
    selectedColor: Color = LocalColors.current.actionText,
    defaultColor: Color = LocalColors.current.primaryText,
    strokeColor: Color = LocalColors.current.divider,
    strokeColorDisabled: Color = LocalColors.current.secondaryText,
    size: Dp = DefaultRadioButtonSize,
    padding: Dp = DefaultRadioButtonPadding,
    strokeWidth: Dp = DefaultRadioStrokeWidth,
) {
    val dotRadius = animateDpAsState(
        targetValue = if (state.isSelected) (size / 4.5f) else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "radioButton"
    )
    val itemColor by animateColorAsState(
        if (state.isEnabled) {
            if (state.isSelected) selectedColor else defaultColor
        } else {
            if (state.isSelected) strokeColorDisabled else defaultColor
        }
    )
    val strokeColor by animateColorAsState(
        if (state.isEnabled) strokeColor else strokeColorDisabled
    )
    val colorBrush = SolidColor(itemColor)
    val strokeBrush = SolidColor(strokeColor)
    val defaultColorBrush = SolidColor(defaultColor)
    Canvas(
        Modifier
            .minimumInteractiveComponentSize()
            .wrapContentSize(Alignment.Center)
            .padding(padding)
            .requiredSize(size)
    ) {
        drawCircle(
            brush = colorBrush,
            radius = (size / 2).toPx()
        )
        val strokePx = strokeWidth.toPx()
        if (strokePx > 0f) {
            drawCircle(
                brush = strokeBrush,
                radius = (size / 2).toPx(),
                style = Stroke(width = strokePx)
            )
        }
        if (dotRadius.value > 0.dp) {
            drawCircle(
                brush = defaultColorBrush,
                radius = dotRadius.value.toPx(),
                style = Fill
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewCheckBox() {
    AppTheme {
        Column {
            RadioButton(modifier = Modifier, AndroidRadioButtonState(isSelected = true))
            RadioButton(modifier = Modifier, AndroidRadioButtonState(isSelected = false))
            RadioButton(
                modifier = Modifier,
                AndroidRadioButtonState(isSelected = true, isEnabled = false)
            )
            RadioButton(
                modifier = Modifier,
                AndroidRadioButtonState(isSelected = false, isEnabled = false)
            )
        }
    }
}