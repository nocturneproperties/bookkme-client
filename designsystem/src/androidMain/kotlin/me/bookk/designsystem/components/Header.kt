package me.bookk.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun Header(text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null) {
    Text(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        textAlign = textAlign,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = LocalColors.current.header
    )
}