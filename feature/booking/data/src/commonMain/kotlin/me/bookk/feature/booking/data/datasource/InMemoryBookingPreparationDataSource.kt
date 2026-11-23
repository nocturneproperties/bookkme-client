package me.bookk.feature.booking.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import kotlin.uuid.Uuid

internal class InMemoryBookingPreparationDataSource :
    BookingPreparationDataSource,
    LogOutAction {

    private val selections = MutableStateFlow<Map<Uuid, BookingSelection>>(emptyMap())

    override fun observeSelection(businessId: Uuid): Flow<BookingSelection> {
        return selections
            .map { it.selectionOf(businessId) }
            .distinctUntilChanged()
    }

    override suspend fun saveSelection(businessId: Uuid, selection: BookingSelection) {
        selections.update { it + (businessId to selection) }
    }

    override suspend fun doOnLogOut() {
        selections.value = emptyMap()
    }

    private fun Map<Uuid, BookingSelection>.selectionOf(businessId: Uuid): BookingSelection {
        return this[businessId] ?: BookingSelection()
    }
}
