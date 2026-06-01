package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignSystemBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    title: String,
    onDismiss: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = LocalColors.current.elevated,
        properties = ModalBottomSheetProperties(),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.primary()
            )
            content()
        }
    }
}