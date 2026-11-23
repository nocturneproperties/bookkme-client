package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingServiceGroup
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class GetBookingCatalogImpl(
    private val getBusinesses: GetBusinesses,
    private val getBusinessServices: GetBusinessServices,
) : GetBookingCatalog {

    override fun flow(businessId: Uuid): Flow<BookingCatalog> {
        return combine(
            getBusinesses.flow(setOf(businessId)),
            getBusinessServices.flow(businessId),
            ::catalog,
        ).distinctUntilChanged()
    }

    override suspend fun refresh(businessId: Uuid): BookingCatalog {
        return coroutineScope {
            val businesses = async { getBusinesses.refresh(setOf(businessId)) }
            val services = async { getBusinessServices.refresh(businessId) }
            catalog(businesses.await(), services.await())
        }
    }

    private fun catalog(businesses: List<Business>, services: List<Service>): BookingCatalog {
        return BookingCatalog(
            businessName = businesses.firstOrNull()?.name,
            groups = services.bookable()
                .groupBy { it.group }
                .entries
                .sortedBy { it.key.createdAt }
                .map { (group, groupServices) -> group(group, groupServices) },
        )
    }

    private fun group(group: ServiceGroup, services: List<Service>): BookingServiceGroup {
        return BookingServiceGroup(
            id = group.id,
            name = group.name,
            services = services.map { it.toBookingService() },
        )
    }
}
