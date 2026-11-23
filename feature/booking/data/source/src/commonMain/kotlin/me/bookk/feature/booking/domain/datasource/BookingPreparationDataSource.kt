package me.bookk.feature.booking.domain.datasource

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface BookingPreparationDataSource {
    fun observeSelection(businessId: Uuid): Flow<BookingSelection>
    suspend fun saveSelection(businessId: Uuid, selection: BookingSelection)
}
