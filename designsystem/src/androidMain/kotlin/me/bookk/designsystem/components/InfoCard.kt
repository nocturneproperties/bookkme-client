package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.simple.InfoLine

@Composable
fun InfoCard(lines: List<InfoLine>, modifier: Modifier = Modifier) {
    Column(modifier) {
        lines.forEachIndexed { index, line ->
            InfoCardLine(line, isFirst = index == 0, isLast = index == lines.lastIndex)
        }
    }
}

@Composable
fun InfoCardLine(line: InfoLine, isFirst: Boolean, isLast: Boolean, modifier: Modifier = Modifier) {
    val onClick = line.onClick
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(infoCardLineShape(isFirst, isLast))
            .background(LocalColors.current.elevated)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = line.title.localized(),
                style = MaterialTheme.typography.bodyLarge.primary()
            )
            Text(
                modifier = Modifier.weight(1f),
                text = line.value.localized(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.secondary()
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = LocalColors.current.divider
            )
        }
    }
}

@Composable
private fun infoCardLineShape(isFirst: Boolean, isLast: Boolean): Shape {
    val shape = MaterialTheme.shapes.large
    return when {
        isFirst && isLast -> shape
        isFirst -> shape.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
        isLast -> shape.copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
        else -> RectangleShape
    }
}
