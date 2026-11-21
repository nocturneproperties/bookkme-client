package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.typography.secondary

enum class CircleSymbol(
    val glyph: String,
) {
    MINUS("-"),
    PLUS("+"),
}

@Composable
fun CircleSymbolButton(
    symbol: CircleSymbol,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: StringDesc? = null,
) {
    val description = contentDescription?.localized()
    TextButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .semantics { description?.let { this.contentDescription = it } },
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .background(AppColors.White.copy(alpha = 0.1f), CircleShape)
                .size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                symbol.glyph,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.secondary(),
            )
        }
    }
}
