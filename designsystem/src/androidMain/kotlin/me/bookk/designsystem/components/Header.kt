package me.bookk.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun Header(text: StringDesc, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        text = text.localized(),
        style = MaterialTheme.typography.titleMedium,
        color = LocalColors.current.header
    )
}