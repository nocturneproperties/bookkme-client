package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import me.bookk.designsystem.uistate.ListState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@Composable
fun <T> List(
    state: ListState<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    item: @Composable (T) -> Unit,
) {
    ListStates(state, modifier) { appearance ->
        state.items.forEach {
            AppearingItem(appearance, key(it)) { item(it) }
        }
    }
}

@Composable
fun <T> CardList(
    state: ListState<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    item: @Composable (T) -> Unit,
) {
    ListStates(state, modifier) { appearance ->
        if (state.items.isEmpty()) return@ListStates
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(0.px)
                .gap(0.px),
        ) {
            state.items.forEachIndexed { index, row ->
                AppearingItem(appearance, key(row)) {
                    if (index > 0) HorizontalDivider()
                    item(row)
                }
            }
        }
    }
}

@Composable
private fun <T> ListStates(
    state: ListState<T>,
    modifier: Modifier,
    content: @Composable (ItemAppearance) -> Unit,
) {
    if (!state.isVisible) return
    val appearance = rememberItemAppearance(
        isShown =
        !state.isInitialLoading || state.errorState != null,
    )
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem)
            .then(modifier),
    ) {
        val errorState = state.errorState
        val emptyState = state.emptyState
        when {
            errorState != null -> ErrorStateView(errorState)
            state.isInitialLoading -> InitialLoadingView()
            state.items.isEmpty() && emptyState != null -> EmptyStateView(emptyState)
            else -> ListContent(state, appearance, content)
        }
    }
}

@Composable
private fun <T> ListContent(
    state: ListState<T>,
    appearance: ItemAppearance,
    content: @Composable (ItemAppearance) -> Unit,
) {
    state.bannerError?.let { BannerErrorView(it) }
    content(appearance)
    val loadMore = state.loadMore ?: return
    LaunchedEffect(state.items.size) { loadMore() }
}
