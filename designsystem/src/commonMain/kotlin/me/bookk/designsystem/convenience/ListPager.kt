package me.bookk.designsystem.convenience

import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState

fun <K> ViewModel.listPager(
    listState: ListState<*>,
    notifications: PresentationNotificationState,
    source: PagedSource<K, *>,
    refreshState: RefreshState? = null,
    onRefresh: (() -> Unit)? = null,
): ListPager<K> {
    return ListPager(this, listState, notifications, source, refreshState, onRefresh)
}

class ListPager<K>(
    private val viewModel: ViewModel,
    private val listState: ListState<*>,
    private val notifications: PresentationNotificationState,
    private val source: PagedSource<K, *>,
    private val refreshState: RefreshState? = null,
    private val onRefresh: (() -> Unit)? = null,
) {
    private var nextPage: NextPage<K>? = null

    fun load(key: K) {
        viewModel.startListLoading(
            listState = listState,
            notifications = notifications,
            refreshState = refreshState,
            onRefresh = onRefresh,
            load = ListLoad(
                call = { source.refresh(key).skippingEmptyPages(key) },
                itemsOf = Page<*>::items,
                onLoaded = weakSelfClosure { pager, page -> pager.onPageLoaded(key, page) },
            ),
        )
    }

    private fun loadNextPage() {
        val page = nextPage ?: return
        viewModel.launch(
            key = listState.loadKey(),
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { listState.bannerError = null },
            call = { source.loadMore(page.key, page.cursor).skippingEmptyPages(page.key) },
            onComplete = { loaded -> onPageLoaded(page.key, loaded) },
            onError = { error ->
                viewModel.showListError(
                    listState,
                    notifications,
                    error,
                    retry = weakSelfClosure { it.loadNextPage() },
                )
            },
        )
    }

    private suspend fun Page<*>.skippingEmptyPages(key: K): Page<*> {
        var page = this
        while (page.items.isEmpty()) {
            val cursor = page.nextCursor ?: return page
            page = source.loadMore(key, cursor)
        }
        return page
    }

    private fun onPageLoaded(key: K, page: Page<*>) {
        nextPage = page.nextCursor?.let { NextPage(key, it) }
        listState.loadMore = if (page.hasMore) weakSelfClosure { it.loadNextPage() } else null
    }

    private class NextPage<K>(
        val key: K,
        val cursor: String,
    )
}
