package me.bookk.feature.booking.domain.impl

import me.bookk.feature.booking.domain.api.SetBookingServices
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import kotlin.uuid.Uuid

internal class SetBookingServicesImpl(
    private val dataSource: BookingPreparationDataSource,
) : SetBookingServices {

    override suspend fun invoke(businessId: Uuid, services: List<PreparedService>) {
        val selected = services.map { it.toSelected() }
        dataSource.saveSelection(businessId, BookingSelection(selected))
    }

    private fun PreparedService.toSelected(): BookingSelection.SelectedService {
        return BookingSelection.SelectedService(
            serviceId = service.id,
            quantity = quantity,
            approvedPrice = service.price,
        )
    }
}
