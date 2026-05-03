package me.bookk.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.uistate.ListState

@Composable
fun <T> List(
    state: ListState<T>,
    modifier: Modifier = Modifier,
    idProvider: ((T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    LazyColumn(modifier) {
        when {
            state.items.isNotEmpty() -> {
                items(state.items, key = idProvider, itemContent = itemContent)
                state.loadMore?.let { loadMore ->
                    item { loadMore() }
                }
            }

            state.isInitialLoading -> {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            state.emptyState != null -> {
                item {
                    EmptyStateView(state.emptyState!!)
                }
            }
        }
    }
}