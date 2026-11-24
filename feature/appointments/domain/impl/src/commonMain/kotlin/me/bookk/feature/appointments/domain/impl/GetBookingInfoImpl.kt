package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.GetBookingInfo
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class GetBookingInfoImpl(
    private val dataSource: ClientBookingDataSource,
) : GetBookingInfo {

    override fun flow(businessId: Uuid): Flow<BookingInfo?> {
        return dataSource.observeBookingInfo(businessId)
    }

    override suspend fun refresh(businessId: Uuid) {
        val info = runCatching { dataSource.getBookingInfo(businessId) }
            .getOrElse { error ->
                if (error is DomainError.NotFound) forgetBookingInfo(businessId)
                throw error
            }
        dataSource.saveBookingInfoInDb(info)
    }

    private suspend fun forgetBookingInfo(businessId: Uuid): Nothing {
        dataSource.deleteBookingInfoInDb(businessId)
        throw GetBookingInfo.Error.AppointmentsNotEnabled()
    }
}
