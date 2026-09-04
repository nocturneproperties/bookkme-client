package me.bookk.designsystem.uistate

import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.simple.ErrorState

interface ListState<T> : ViewState {
    val items: List<T>
    var loadMore: (() -> Unit)?
    var emptyState: EmptyState?
    var errorState: ErrorState?
    var isInitialLoading: Boolean

    fun append(list: List<T>)
    fun replace(list: List<T>)
    fun clear()
}