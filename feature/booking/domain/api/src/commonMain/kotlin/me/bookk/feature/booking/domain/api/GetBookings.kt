package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod

interface GetBookings : PagedSource<BookingPeriod, BookingOverview> {
    fun flow(period: BookingPeriod): Flow<List<BookingOverview>>
}
