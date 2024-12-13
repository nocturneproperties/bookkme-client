package me.bookk.designsystem.components

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun AnimatedLinearProgress(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
) {
    Box(
        modifier = modifier
            .height(4.dp)
            .background(
                color = LocalColors.current.Divider,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        val progressWidth by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            label = "stepper_width"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressWidth)
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = LocalColors.current.PrimaryText
                )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedLinearProgress(progress = 0f)
                AnimatedLinearProgress(progress = 0.5f)
                AnimatedLinearProgress(progress = 1f)
            }
        }
    }
}