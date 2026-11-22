package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary

@Composable
fun InfoNote(text: String, modifier: Modifier = Modifier, title: String? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(LocalColors.current.actionText.copy(alpha = BACKGROUND_ALPHA))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = LocalColors.current.actionText,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge.primary(),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(text = text, style = MaterialTheme.typography.bodyLarge.primary())
        }
    }
}

private const val BACKGROUND_ALPHA = 0.2f
