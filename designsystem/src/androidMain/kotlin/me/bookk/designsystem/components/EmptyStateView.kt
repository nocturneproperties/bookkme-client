package me.bookk.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.painter
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.simple.EmptyState

@Composable
fun LazyItemScope.EmptyStateView(state: EmptyState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = state.image.painter(),
            contentDescription = state.label.localized()
        )
        Text(
            state.label.localized(),
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
        Image(
            painter = state.image.painter(),
            contentDescription = state.label.localized()
        )
        Text(
            state.label.localized(),
            style = MaterialTheme.typography.bodyMedium.secondary()
        )
    }
}