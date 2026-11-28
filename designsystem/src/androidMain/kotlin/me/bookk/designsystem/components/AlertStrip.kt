package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.ButtonState

@Composable
fun AlertStrip(
    text: StringDesc,
    action: ButtonState,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColors.current
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.background)
            .background(colors.error.copy(alpha = TINT_ALPHA)),
    ) {
        HorizontalDivider(color = colors.error.copy(alpha = LINE_ALPHA))
        Row(
            Modifier
                .heightIn(min = 44.dp)
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = colors.error,
            )
            Text(
                text = text.localized(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = colors.error,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            AlertStripAction(action)
        }
    }
}

@Composable
private fun AlertStripAction(action: ButtonState) {
    val color = LocalColors.current.error
    if (action.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .size(16.dp),
            strokeWidth = 2.dp,
            color = color,
        )
    } else {
        TextButton(
            onClick = { action.onClick?.invoke() },
            enabled = action.isEnabled,
        ) {
            Text(
                text = action.text.localized(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

private const val TINT_ALPHA = 0.12f
private const val LINE_ALPHA = 0.2f
