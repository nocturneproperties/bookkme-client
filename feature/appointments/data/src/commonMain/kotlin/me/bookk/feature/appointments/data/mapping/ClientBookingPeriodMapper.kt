package me.bookk.feature.appointments.data.mapping

import me.bookk.feature.appointments.data.remote.model.AppointmentRequestStatusRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentStatusRemote
import me.bookk.feature.appointments.data.remote.model.BookingPeriodRemote
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod

internal fun ClientBookingPeriod.toRemote(): BookingPeriodRemote? {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> BookingPeriodRemote.UPCOMING
        ClientBookingPeriod.PAST -> null
    }
}

internal fun ClientBookingPeriod.appointmentStatuses(): List<AppointmentStatusRemote> {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> listOf(AppointmentStatusRemote.SCHEDULED)

        ClientBookingPeriod.PAST -> listOf(
            AppointmentStatusRemote.COMPLETED,
            AppointmentStatusRemote.CANCELLED,
            AppointmentStatusRemote.NO_SHOW,
            AppointmentStatusRemote.UNRESOLVED,
        )
    }
}

internal fun ClientBookingPeriod.requestStatuses(): List<AppointmentRequestStatusRemote> {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> listOf(AppointmentRequestStatusRemote.PENDING)

        ClientBookingPeriod.PAST -> listOf(
            AppointmentRequestStatusRemote.DECLINED,
            AppointmentRequestStatusRemote.CANCELLED,
        )
    }
}

internal fun ClientBookingPeriod.appointmentStatusNames(): List<String> {
    return appointmentStatuses().map { it.name }
}

internal fun ClientBookingPeriod.requestStatusNames(): List<String> {
    return requestStatuses().map { it.name }
}
