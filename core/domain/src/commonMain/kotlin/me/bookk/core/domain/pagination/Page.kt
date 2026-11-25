package me.bookk.core.domain.pagination

data class Page<T>(
    val items: List<T>,
    val nextCursor: String?,
) {
    val hasMore: Boolean
        get() = nextCursor != null
}
