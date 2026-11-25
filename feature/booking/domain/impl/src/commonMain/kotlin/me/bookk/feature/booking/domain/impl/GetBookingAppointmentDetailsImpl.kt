package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientAppointment
import me.bookk.feature.booking.domain.api.GetBookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails.Status
import kotlin.uuid.Uuid

internal class GetBookingAppointmentDetailsImpl(
    private val getClientAppointment: GetClientAppointment,
) : GetBookingAppointmentDetails {

    override fun flow(appointmentId: Uuid): Flow<BookingAppointmentDetails?> {
        return getClientAppointment.flow(appointmentId)
            .map { cached -> cached?.takeIf { it.appointment.services.isNotEmpty() }?.toDetails() }
            .distinctUntilChanged()
    }

    private fun ClientAppointment.toDetails(): BookingAppointmentDetails {
        val visit = BookedVisit(
            businessName = businessName,
            employee = appointment.employee,
            startsAt = appointment.date.toInstant(TimeZone.currentSystemDefault()),
            duration = appointment.services.totalDuration(),
            services = appointment.providedServices,
            total = appointment.priceAdjustment?.price ?: appointment.services.totalPrice(),
            note = appointment.clientNote,
        )
        return BookingAppointmentDetails(
            appointment.id,
            visit.toSummary(),
            appointment.status.toDetails(),
        )
    }

    private fun AppointmentStatus.toDetails(): Status {
        return when (this) {
            AppointmentStatus.SCHEDULED -> Status.CONFIRMED
            AppointmentStatus.COMPLETED, AppointmentStatus.UNRESOLVED -> Status.COMPLETED
            AppointmentStatus.CANCELLED -> Status.CANCELLED
            AppointmentStatus.NO_SHOW -> Status.MISSED
        }
    }
}
