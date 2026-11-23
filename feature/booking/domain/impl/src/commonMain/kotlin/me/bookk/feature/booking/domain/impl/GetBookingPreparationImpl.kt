package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class GetBookingPreparationImpl(
    private val dataSource: BookingPreparationDataSource,
    private val getBusinessServices: GetBusinessServices,
) : GetBookingPreparation {

    override fun flow(businessId: Uuid): Flow<BookingPreparation> {
        return combine(
            dataSource.observeSelection(businessId),
            getBusinessServices.flow(businessId),
            ::preparation,
        ).distinctUntilChanged()
    }

    private fun preparation(
        selection: BookingSelection,
        services: List<Service>,
    ): BookingPreparation {
        val offered = services.bookable().associateBy { it.id }
        return BookingPreparation(
            services = selection.services.mapNotNull { picked ->
                offered[picked.serviceId]?.let {
                    BookingPreparation.PreparedService(it.toBookingService(), picked.quantity)
                }
            },
            specialistId = selection.specialistId,
        )
    }
}
