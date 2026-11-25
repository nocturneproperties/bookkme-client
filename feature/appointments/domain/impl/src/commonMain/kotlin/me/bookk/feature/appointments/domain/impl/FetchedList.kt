package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.pagination.Page
import kotlin.time.Instant

internal class FetchedList<T>(
    private val position: ListPosition,
    private val page: Page<T>?,
    private val dateOf: (T) -> Instant,
) {
    val lastDateWithMore: Instant?
        get() = page?.takeIf { it.hasMore }?.items?.lastOrNull()?.let(dateOf)

    fun itemsWithin(isWithin: (Instant) -> Boolean): List<T> {
        return page?.items.orEmpty().filter { isWithin(dateOf(it)) }
    }

    fun nextPosition(keptCount: Int): ListPosition {
        val page = page ?: return ListPosition.End
        if (keptCount < page.items.size) return position
        return page.nextCursor?.let { ListPosition.At(it) } ?: ListPosition.End
    }
}

internal suspend fun <T> ListPosition.fetch(
    dateOf: (T) -> Instant,
    load: suspend (cursor: String?) -> Page<T>,
): FetchedList<T> {
    val page = when (this) {
        ListPosition.Start -> load(null)
        is ListPosition.At -> load(cursor)
        ListPosition.End -> null
    }
    return FetchedList(this, page, dateOf)
}
