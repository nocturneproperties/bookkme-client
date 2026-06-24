package me.bookk.designsystem.modifier

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors

fun Modifier.drawShadow(
    color: Color = Color.Black,
    alpha: Float = 0.05f,
    borderRadius: Dp = 12.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 30.dp,
    spread: Dp = 0.dp,
    enabled: Boolean = true,
): Modifier {
    if (!enabled) return this

    return drawBehind {
        val transparentColor = color.copy(alpha = 0.0f).toArgb()
        val shadowColor = color.copy(alpha = alpha).toArgb()
        val spreadPx = spread.toPx()

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.nativePaint
            frameworkPaint.color = transparentColor
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )

            canvas.save()

            if (spreadPx > 0) {
                val spreadScaleX = 1f + (2f * spreadPx / size.width)
                val spreadScaleY = 1f + (2f * spreadPx / size.height)
                canvas.scale(
                    spreadScaleX,
                    spreadScaleY,
                    size.width / 2,
                    size.height / 2
                )
            }

            canvas.drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )

            canvas.restore()
        }
    }
}

fun Modifier.coloredOverlay(
    brush: Brush
): Modifier {
    return graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush, blendMode = BlendMode.SrcAtop)
            }
        }
}

@Composable
fun Modifier.coloredOverlay(
    brushProvider: (IntSize) -> Brush
): Modifier {
    var brush by remember { mutableStateOf<Brush>(SolidColor(Color.Transparent)) }
    return onGloballyPositioned { brush = brushProvider(it.size) }
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush, blendMode = BlendMode.SrcAtop)
            }
        }
}

@Composable
fun Modifier.topShadow(offset: Dp, colorFrom: Color, colorTo: Color = Color.Transparent): Modifier {
    val pxOffset = with(LocalDensity.current) { offset.toPx() }
    return coloredOverlay {
        Brush.verticalGradient(
            startY = 0f,
            endY = pxOffset,
            colorStops = arrayOf(0f to colorFrom, 1f to colorTo)
        )
    }
}

@Composable
fun Modifier.bottomShadow(offset: Dp, colorTo: Color, colorFrom: Color = Color.Transparent): Modifier {
    val pxOffset = with(LocalDensity.current) { offset.toPx() }
    return coloredOverlay {
        Brush.verticalGradient(
            startY = (it.height - pxOffset).coerceAtLeast(0f),
            endY = it.height.toFloat(),
            colorStops = arrayOf(0f to colorFrom, 1f to colorTo)
        )
    }
}

fun Modifier.shimmerBackground(
    color: Color = Color(0xFFF0F2F4),
    duration: Int = 800,
): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmerEffectTransition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(duration)
        ),
        label = "shimmerEffectOffset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                color,
                color.copy(alpha = 0.6f),
                color
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned { size = it.size }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.drawShadow(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.White
                )
            ) {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "Card with \"drawShadow\" modifier"
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .shimmerBackground(),
            ) {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "Card with \"shimmerBackground\" modifier"
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.White
                )
            ) {
                Text(
                    modifier = Modifier
                        .padding(24.dp)
                        .coloredOverlay(SolidColor(LocalColors.current.actionText)),
                    text = "Text with \"coloredOverlay\" modifier"
                )
            }
        }
    }
}