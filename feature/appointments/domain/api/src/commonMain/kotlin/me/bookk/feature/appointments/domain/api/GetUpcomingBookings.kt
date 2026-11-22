package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking

interface GetUpcomingBookings {
    fun flow(): Flow<List<UpcomingBooking>>
    suspend fun refresh(): List<UpcomingBooking>
}
