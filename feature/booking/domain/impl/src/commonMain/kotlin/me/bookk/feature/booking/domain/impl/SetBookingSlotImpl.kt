package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.booking.domain.api.SetBookingSlot
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class SetBookingSlotImpl(
    private val dataSource: BookingPreparationDataSource,
) : SetBookingSlot {

    override suspend fun invoke(businessId: Uuid, start: Instant) {
        val selection = dataSource.observeSelection(businessId).first()
        dataSource.saveSelection(businessId, selection.copy(slotStart = start))
    }
}
