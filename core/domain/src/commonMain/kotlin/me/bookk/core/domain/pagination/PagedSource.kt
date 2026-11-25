package me.bookk.core.domain.pagination

interface PagedSource<K, T> {
    suspend fun refresh(key: K): Page<T>

    suspend fun loadMore(key: K, cursor: String): Page<T>
}
