package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import kotlin.uuid.Uuid

interface GetBookingAppointmentDetails {
    fun flow(appointmentId: Uuid): Flow<BookingAppointmentDetails?>
}
