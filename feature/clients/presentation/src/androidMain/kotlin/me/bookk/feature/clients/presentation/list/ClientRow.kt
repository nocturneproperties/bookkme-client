package me.bookk.feature.clients.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary

@Composable
internal fun ClientRow(modifier: Modifier, item: ClientListItem) {
    Column(modifier.fillMaxWidth()) {
        TextButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            onClick = item.onClick,
            shape = RectangleShape,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                text = item.fullName,
                style = MaterialTheme.typography.bodyLarge.primary(),
                textAlign = TextAlign.Start,
            )
        }
        HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
    }
}
