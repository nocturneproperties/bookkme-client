package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import me.bookk.designsystem.uistate.ListState
import org.jetbrains.compose.web.css.cssRem

@Composable
fun <T> List(state: ListState<T>, modifier: Modifier = Modifier, item: @Composable (T) -> Unit) {
    if (!state.isVisible) return
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem)
            .then(modifier),
    ) {
        val errorState = state.errorState
        val emptyState = state.emptyState
        when {
            state.isInitialLoading -> InitialLoadingView()
            errorState != null -> ErrorStateView(errorState)
            state.items.isEmpty() && emptyState != null -> EmptyStateView(emptyState)
            else -> ListContent(state, item)
        }
    }
}

@Composable
private fun <T> ListContent(state: ListState<T>, item: @Composable (T) -> Unit) {
    state.bannerError?.let { BannerErrorView(it) }
    state.items.forEach { item(it) }
    val loadMore = state.loadMore ?: return
    LaunchedEffect(state.items.size) { loadMore() }
}
