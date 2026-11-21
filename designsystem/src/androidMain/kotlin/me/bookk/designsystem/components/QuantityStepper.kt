package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.typography.primary

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CircleSymbolButton(
            symbol = CircleSymbol.MINUS,
            onClick = onDecrease,
            contentDescription = DesignSystem.strings.quantity_decrease.desc(),
        )
        Text(
            quantity.toString(),
            modifier = Modifier
                .widthIn(min = 16.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.primary(),
        )
        CircleSymbolButton(
            symbol = CircleSymbol.PLUS,
            onClick = onIncrease,
            contentDescription = DesignSystem.strings.quantity_increase.desc(),
        )
    }
}
