package me.bookk.designsystem.components

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.uistate.AndroidButtonState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorBottomSheet(
    sheetState: SheetState,
    title: StringDesc,
    data: List<T>,
    preselectItem: T? = null,
    requireConfirmation: Boolean = true,
    onDismiss: () -> Unit = {},
    onItemPicked: (T) -> Unit = {},
    itemContent: @Composable (index: Int, item: T, isSelected: Boolean, onClick: (T) -> Unit) -> Unit
) {
    if (data.isNotEmpty()) {
        DesignSystemBottomSheet(
            sheetState = sheetState,
            title = title.localized(),
            onDismiss = onDismiss
        ) {
            var selectedItem: T? by remember { mutableStateOf(preselectItem) }
            Column(Modifier.verticalScroll(rememberScrollState())) {
                data.forEachIndexed { index, item ->
                    itemContent(index, item, selectedItem == item) { item ->
                        selectedItem = item
                        if (!requireConfirmation) {
                            onItemPicked(item)
                        }
                    }
                }
            }
            if (requireConfirmation) {
                val buttonState = remember(selectedItem) {
                    AndroidButtonState(
                        text = DesignSystem.strings.action_continue.desc(),
                        isEnabled = selectedItem != null
                    )
                }
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = buttonState,
                    onClick = { selectedItem?.let { onItemPicked(it) } }
                )
            }
        }
    }
}

@Composable
fun <T> StandardSelectorItem(
    item: T,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    stringify: @Composable (T) -> String = { it.toString() },
    icon: @Composable (Modifier) -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(bottom = 4.dp)
            .height(56.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = LocalColors.current.elevated
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon(Modifier.size(40.dp))
            val text = stringify(item)
            Text(
                text = text,
                modifier = Modifier.padding(2.dp).weight(1f),
                style = MaterialTheme.typography.bodyLarge.primary(),
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = LocalColors.current.actionText,
                    contentDescription = "Selected icon"
                )
            }
        }
    }
}

@Composable
fun <T> StandardElevatedSelectorItem(
    item: T,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    stringify: @Composable (T) -> String = { it.toString() },
    icon: @Composable (Modifier) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(72.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = LocalColors.current.elevated
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon(Modifier.size(40.dp))
            val text = stringify(item)
            Text(
                text = text,
                modifier = Modifier.padding(2.dp).weight(1f),
                style = MaterialTheme.typography.bodyLarge.primary(),
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = LocalColors.current.actionText,
                    contentDescription = "Selected icon"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(backgroundColor = Color.BLACK.toLong())
@Composable
private fun Preview() {
    AppTheme {
        SelectorBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            title = "Title".desc(),
            data = listOf("Item 1", "Item 2"),
            onDismiss = {},
            itemContent = { i: Int, item: String, isSelected: Boolean, onClick: (String) -> Unit ->
                StandardSelectorItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onClick(item) }
                )
            },
            requireConfirmation = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(backgroundColor = Color.BLACK.toLong())
@Composable
private fun PreviewDark() {
    AppTheme(ThemeMode.DARK) {
        SelectorBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            title = "Title".desc(),
            data = listOf("Item 1", "Item 2"),
            onDismiss = {},
            itemContent = { i: Int, item: String, isSelected: Boolean, onClick: (String) -> Unit ->
                StandardElevatedSelectorItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onClick(item) }
                )
            },
            requireConfirmation = false
        )
    }
}