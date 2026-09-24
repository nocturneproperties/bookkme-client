package me.bookk.designsystem.convenience

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CancellationException
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.simple.ErrorState

internal class UnauthorizedTestError : Exception()

internal class FakeErrorMapper : ErrorMapper {
    override fun mapToNotification(e: Throwable): PresentationNotification = when (e) {
        is UnauthorizedTestError -> PresentationNotification.Unauthorized
        is CancellationException -> PresentationNotification.Ignore
        else -> PresentationNotification.GlobalMessage(text = "error".desc())
    }

    override fun mapToDescription(e: Throwable) = ErrorDescription(
        title = "title of ${e::class.simpleName}".desc(),
        message = "message of ${e::class.simpleName}".desc()
    )
}

internal class TestListViewModel : ViewModel(VmArgs(FakeErrorMapper())) {
    fun load(
        listState: ListState<*>,
        notifications: PresentationNotificationState,
        refreshState: RefreshState? = null,
        onRefresh: (() -> Unit)? = null,
        call: suspend () -> Collection<*>,
    ) = loadList(listState, notifications, refreshState, onRefresh, call)
}

internal class FakeListState<T>(initialItems: List<T> = emptyList()) : ListState<T> {
    override val items = initialItems.toMutableList()
    override var loadMore: (() -> Unit)? = null
    override var emptyState: EmptyState? = null
    override var errorState: ErrorState? = null
    override var bannerError: BannerErrorState? = null
    override var isInitialLoading: Boolean = initialItems.isEmpty()
    override var id: String = "list"
    override var isVisible: Boolean = true

    override fun append(list: List<T>) {
        items += list
        isInitialLoading = false
    }

    override fun replace(list: List<T>) {
        items.clear()
        items += list
        isInitialLoading = false
    }

    override fun clear() {
        items.clear()
    }

    override fun refreshIdentity() = Unit
}

internal class FakeRefreshState : RefreshState {
    override var isRefreshing: Boolean = false
    override var onRefresh: () -> Unit = {}
}

internal class FakeNotificationState : PresentationNotificationState {
    override val presentationNotification = mutableListOf<PresentationNotification>()

    override fun add(notification: PresentationNotification) {
        presentationNotification += notification
    }

    override fun removeFirst() {
        presentationNotification.removeAt(0)
    }
}
