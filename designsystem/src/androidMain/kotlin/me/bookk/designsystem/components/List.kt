package me.bookk.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.uistate.ListState

@Composable
fun <T> List(
    state: ListState<T>,
    modifier: Modifier = Modifier,
    idProvider: ((T) -> Any)? = null,
    contentType: (T) -> Any? = { null },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    LazyColumn(
        modifier,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect
    ) {
        item {
            var lastBannerError by remember { mutableStateOf(state.bannerError) }
            if (state.bannerError != null) lastBannerError = state.bannerError
            AnimatedVisibility(
                visible = state.bannerError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                lastBannerError?.let { BannerErrorView(it) }
            }
        }
        when {
            state.errorState != null -> {
                item {
                    ErrorStateView(state.errorState!!)
                }
            }

            state.items.isNotEmpty() -> {
                items(state.items, key = idProvider, itemContent = itemContent, contentType = contentType)
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