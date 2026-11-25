package me.bookk.feature.booking.domain.impl

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails

internal fun Appointment.toVisit(): BookedVisit {
    return BookedVisit(
        businessName = businessName,
        employee = employee,
        startsAt = date.toInstant(TimeZone.currentSystemDefault()),
        duration = services.totalDuration(),
        services = providedServices,
        total = priceAdjustment?.price ?: services.totalPrice(),
        note = clientNote,
    )
}

internal fun AppointmentRequest.toVisit(): BookedVisit {
    return BookedVisit(
        businessName = businessName,
        employee = employee,
        startsAt = date,
        duration = services.totalDuration(),
        services = services,
        total = services.totalPrice(),
        note = note,
    )
}

internal fun AppointmentStatus.toBookingStatus(): BookingAppointmentDetails.Status {
    return when (this) {
        AppointmentStatus.SCHEDULED -> {
            BookingAppointmentDetails.Status.CONFIRMED
        }

        AppointmentStatus.COMPLETED, AppointmentStatus.UNRESOLVED -> {
            BookingAppointmentDetails.Status.COMPLETED
        }

        AppointmentStatus.CANCELLED -> {
            BookingAppointmentDetails.Status.CANCELLED
        }

        AppointmentStatus.NO_SHOW -> {
            BookingAppointmentDetails.Status.MISSED
        }
    }
}

internal fun AppointmentRequestStatus.toBookingStatus(): BookingRequestDetails.Status {
    return when (this) {
        AppointmentRequestStatus.PENDING -> BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION
        AppointmentRequestStatus.APPROVED -> BookingRequestDetails.Status.CONFIRMED
        AppointmentRequestStatus.DECLINED -> BookingRequestDetails.Status.DECLINED
        AppointmentRequestStatus.CANCELLED -> BookingRequestDetails.Status.CLOSED
    }
}
