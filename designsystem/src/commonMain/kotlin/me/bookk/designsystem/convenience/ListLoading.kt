package me.bookk.designsystem.convenience

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.withIndex
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.ErrorState
import kotlin.time.Duration.Companion.milliseconds

internal val RENDER_GRACE_PERIOD = 500.milliseconds

fun ViewModel.loadList(
    listState: ListState<*>,
    notifications: PresentationNotificationState,
    refreshState: RefreshState? = null,
    onRefresh: (() -> Unit)? = null,
    call: suspend () -> Collection<*>,
) {
    refreshState?.onRefresh = weakVMClosure {
        onRefresh?.invoke()
        it.launchListLoad(listState, notifications, refreshIndicator = refreshState, call = call)
    }
    launchListLoad(listState, notifications, refreshIndicator = null, call = call)
}

private fun ViewModel.launchListLoad(
    listState: ListState<*>,
    notifications: PresentationNotificationState,
    refreshIndicator: RefreshState?,
    retriedBanner: BannerErrorState? = null,
    call: suspend () -> Collection<*>,
) {
    val retry = weakVMClosure {
        it.launchListLoad(listState, notifications, refreshIndicator = null, retriedBanner = listState.bannerError, call = call)
    }
    launch(
        key = "loadList@${listState.hashCode()}",
        launchIn = DispatcherProvider.io,
        onStart = {
            refreshIndicator?.isRefreshing = true
            listState.errorState = null
            listState.bannerError = retriedBanner?.retrying()
        },
        call = call,
        onComplete = { result ->
            if (result.isNotEmpty() && listState.isInitialLoading) delay(RENDER_GRACE_PERIOD)
            listState.isInitialLoading = false
            listState.bannerError = null
        },
        onError = { error ->
            if (retriedBanner != null) listState.bannerError = retriedBanner
            when (val notification = error.notification()) {
                PresentationNotification.Ignore -> Unit
                PresentationNotification.Unauthorized -> notifications.add(notification)
                PresentationNotification.BusinessAccessSuspended -> {
                    notifications.add(notification)
                    listState.showLoadingError(error.description(), retry)
                }
                else -> listState.showLoadingError(error.description(), retry)
            }
        },
        onTerminate = {
            refreshIndicator?.isRefreshing = false
        }
    )
}

private fun ListState<*>.showLoadingError(description: ErrorDescription, retry: () -> Unit) {
    if (isInitialLoading) {
        errorState = ErrorState(
            title = description.title,
            subtitle = description.message,
            onRetryClick = retry
        )
    } else {
        bannerError = BannerErrorState.default(onRetryClick = retry)
    }
}

fun <K> Flow<K>.resetListOnChange(listState: ListState<*>): Flow<K> =
    distinctUntilChanged()
        .withIndex()
        .onEach { if (it.index > 0) listState.resetToInitialLoading() }
        .map { it.value }

private fun ListState<*>.resetToInitialLoading() {
    clear()
    isInitialLoading = true
    errorState = null
    bannerError = null
}
