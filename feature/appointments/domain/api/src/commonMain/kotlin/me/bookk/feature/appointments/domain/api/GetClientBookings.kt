package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod

interface GetClientBookings : PagedSource<ClientBookingPeriod, ClientBooking> {
    fun flow(period: ClientBookingPeriod): Flow<List<ClientBooking>>
}
