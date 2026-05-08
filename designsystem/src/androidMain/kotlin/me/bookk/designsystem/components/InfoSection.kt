package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.simple.InfoLine

@Composable
fun InfoSection(line: InfoLine, modifier: Modifier = Modifier) {
    val content: @Composable ColumnScope.() -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                text = line.title.localized(),
                style = MaterialTheme.typography.bodyLarge.secondary()
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = line.value.localized(),
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(
                Modifier.padding(top = 4.dp),
                color = LocalColors.current.divider
            )
        }
    }
    if (line.onClick != null) {
        Card(
            onClick = line.onClick,
            modifier = modifier,
            content = content,
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        )
    } else {
        Card(
            modifier = modifier,
            content = content,
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        )
    }
}