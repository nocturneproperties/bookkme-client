package me.bookk.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import me.bookk.designsystem.modifier.drawShadow
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    background: Color = LocalColors.current.elevated,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier
            .drawShadow()
            .clip(shape),
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {
        content()
    }
}