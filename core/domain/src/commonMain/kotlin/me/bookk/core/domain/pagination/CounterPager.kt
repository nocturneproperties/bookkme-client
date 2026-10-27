package me.bookk.core.domain.pagination

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class CounterPager<R : CounterPager.Request, T>(
    protected val countPerPage: Int,
) {
    private var request: R? = null
    protected var pageData = PageInfo(0)
    private var isLoading: Boolean = false
    private val mutex = Mutex()

    suspend fun reload(request: R): List<T> {
        if (isLoading) throw IllegalStateException("Cancel first")
        mutex.withLock {
            this.request = request
            pageData = PageInfo(countPerPage)
        }
        return loadContentPage(request)
    }

    suspend fun loadMore(): List<T> {
        val request = mutex.withLock {
            if (pageData.isAllDataLoaded) return emptyList()
            if (isLoading) throw CounterPagerException("Pager is already loading")
            request ?: throw CounterPagerException("Call reload first")
        }

        return loadContentPage(request)
    }

    private suspend fun loadContentPage(request: R): List<T> {
        return loadContent(request).also {
            mutex.withLock { pageData.update(it.size) }
        }
    }

    protected abstract suspend fun loadContent(request: R): List<T>

    interface Request {
        abstract override fun equals(other: Any?): Boolean
        abstract override fun hashCode(): Int
    }

    class CounterPagerException(override val message: String) : Throwable()

    protected class PageInfo(
        private val countPerPage: Int,
    ) {
        var loadedItemCount: Int = 0
            private set
        var isAllDataLoaded: Boolean = false
            private set

        var page: Int = 0
            private set
        fun update(size: Int) {
            isAllDataLoaded = size < countPerPage
            loadedItemCount += size
            page += 1
        }
    }
}