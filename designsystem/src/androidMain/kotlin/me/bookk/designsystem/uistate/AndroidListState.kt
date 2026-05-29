package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.simple.EmptyState

class AndroidListState<T>(
    items: List<T> = emptyList()
) : ListState<T> {

    override val items = mutableStateListOf<T>().apply {
        addAll(items)
    }
    override var loadMore: (() -> Unit)? by mutableStateOf(null)
    override var emptyState: EmptyState? by mutableStateOf(null)
    override var isInitialLoading: Boolean by mutableStateOf(true)

    override fun append(list: List<T>) {
        items += list
        isInitialLoading = false
    }

    override fun replace(list: List<T>) {
        items.clear()
        items.addAll(list)
        isInitialLoading = false
    }

    override fun clear() {
        items.clear()
    }
}