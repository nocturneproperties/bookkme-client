package me.bookk.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors

private val DefaultRadioButtonSize = 20.dp
private val DefaultRadioButtonPadding = 2.dp
private val DefaultRadioStrokeWidth = 2.dp

@Composable
fun AppRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selectedColor: Brush = SolidColor(LocalColors.current.PrimaryText),
    defaultColor: Brush = SolidColor(LocalColors.current.PrimaryText),
    size: Dp = DefaultRadioButtonSize,
    padding: Dp = DefaultRadioButtonPadding,
    strokeWidth: Dp = DefaultRadioStrokeWidth,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val dotRadius = animateDpAsState(
        targetValue = if (selected) (size / 2) - (strokeWidth * 2) else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "radioButton"
    )

    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = size / 2
                )
            )
        } else {
            Modifier
        }

    Canvas(
        modifier
            .then(
                if (onClick != null) {
                    Modifier.minimumInteractiveComponentSize()
                } else {
                    Modifier
                }
            )
            .then(selectableModifier)
            .wrapContentSize(Alignment.Center)
            .padding(padding)
            .requiredSize(size)
    ) {
        val strokePx = strokeWidth.toPx()
        drawCircle(
            brush = if (selected) selectedColor else defaultColor,
            radius = (size / 2).toPx() - strokePx / 2,
            style = Stroke(strokePx)
        )
        if (dotRadius.value > 0.dp) {
            drawCircle(
                brush = if (selected) selectedColor else defaultColor,
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
            AppRadioButton(modifier = Modifier, selected = true)
            AppRadioButton(modifier = Modifier, selected = false)
        }
    }
}