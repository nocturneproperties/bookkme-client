package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.simple.EmptyState

@Composable
fun LazyItemScope.EmptyStateView(state: EmptyState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Inbox,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LocalColors.current.secondaryText
        )
        Text(
            state.label.localized(),
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.bodyMedium.secondary()
        )
    }
}

@Composable
fun EmptyStateView(state: EmptyState, modifier: Modifier = Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Inbox,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LocalColors.current.secondaryText
        )
        Text(
            state.label.localized(),
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.bodyMedium.secondary()
        )
    }
}