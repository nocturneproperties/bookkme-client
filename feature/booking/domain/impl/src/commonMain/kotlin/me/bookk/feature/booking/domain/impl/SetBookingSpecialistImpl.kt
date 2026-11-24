package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import kotlin.uuid.Uuid

internal class SetBookingSpecialistImpl(
    private val dataSource: BookingPreparationDataSource,
) : SetBookingSpecialist {

    override suspend fun invoke(businessId: Uuid, specialistId: Uuid) {
        val selection = dataSource.observeSelection(businessId).first()
        val slotStart = selection.slotStart.takeIf { selection.specialistId == specialistId }
        dataSource.saveSelection(
            businessId,
            selection.copy(specialistId = specialistId, slotStart = slotStart),
        )
    }
}
