package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.booking.domain.api.GetBookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import kotlin.uuid.Uuid

internal class GetBookingAppointmentDetailsImpl(
    private val getClientAppointment: GetClientAppointment,
) : GetBookingAppointmentDetails {

    override fun flow(appointmentId: Uuid): Flow<BookingAppointmentDetails?> {
        return getClientAppointment.flow(appointmentId)
            .map { cached -> cached?.takeIf { it.services.isNotEmpty() }?.toDetails() }
            .distinctUntilChanged()
    }

    private fun Appointment.toDetails(): BookingAppointmentDetails {
        return BookingAppointmentDetails(id, toVisit().toSummary(), status.toBookingStatus())
    }
}
