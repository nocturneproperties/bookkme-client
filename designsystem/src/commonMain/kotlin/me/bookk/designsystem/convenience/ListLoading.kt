package me.bookk.designsystem.convenience

import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.ErrorState

fun <T, Data>  ViewModel.loadCachedList(
    listState: ListState<T>,
    refreshState: RefreshState? = null,
    call: suspend (suspend (Data) -> Unit) -> Unit,
    onComplete: (suspend (Data) -> Unit),
) {
    launchCached(
        launchIn = DispatcherProvider.io,
        onStart = {
            refreshState?.isRefreshing = true
            listState.errorState = null
        },
        call = { call(it) },
        onComplete = {
            listState.isInitialLoading = false
            onComplete(it)
        },
        onTerminate = { refreshState?.isRefreshing = false },
        onError = {
            if (listState.isInitialLoading) {
                listState.errorState = ErrorState.default(
                    onRetryClick = weakVMClosure {
                        it.loadCachedList(listState, refreshState, call, onComplete)
                    }
                )
            }
        }
    )
}

fun <T> ViewModel.loadList(
    listState: ListState<T>,
    refreshState: RefreshState? = null,
    call: suspend () -> Unit,
    onComplete: (suspend () -> Unit)? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
) {
    launch(
        launchIn = DispatcherProvider.io,
        onStart = {
            refreshState?.isRefreshing = true
            listState.errorState = null
            listState.bannerError = null
        },
        call = call,
        onComplete = { onComplete?.invoke() },
        onError = { error ->
            if (listState.items.isEmpty()) {
                listState.errorState = ErrorState.default(
                    onRetryClick = weakVMClosure {
                        it.loadList(listState, refreshState, call, onComplete, onError)
                    }
                )
            } else {
                listState.bannerError = BannerErrorState.default(
                    onRetryClick = weakVMClosure {
                        it.loadList(listState, refreshState, call, onComplete, onError)
                    }
                )
                onError?.invoke(error)
            }
        },
        onTerminate = {
            refreshState?.isRefreshing = false
            listState.isInitialLoading = false
        }
    )
}
