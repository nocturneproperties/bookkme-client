package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import kotlin.uuid.Uuid

interface ClientBookingDataSource {
    suspend fun getUpcomingBookings(): UpcomingBookings

    suspend fun saveUpcomingBookingsInDb(bookings: UpcomingBookings)

    fun observeUpcomingAppointments(): Flow<List<Appointment>>

    fun observeUpcomingRequests(): Flow<List<AppointmentRequest>>

    suspend fun getSavedBusinesses(): List<SavedBusinessEntry>

    suspend fun saveSavedBusinessesInDb(businesses: List<SavedBusinessEntry>)

    fun observeSavedBusinesses(): Flow<List<SavedBusinessEntry>>

    suspend fun saveBusiness(businessId: Uuid)
}
