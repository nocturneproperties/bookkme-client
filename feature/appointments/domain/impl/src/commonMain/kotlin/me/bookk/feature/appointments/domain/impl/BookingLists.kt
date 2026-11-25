package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod

internal class BookingLists(
    val appointments: List<Appointment>,
    val requests: List<AppointmentRequest>,
) {
    fun listed(period: ClientBookingPeriod): List<ClientBooking> {
        val visits = appointments.map { ClientBooking.Visit(it) }
        val asked = requests.map { ClientBooking.Request(it) }
        return (visits + asked).sortedWith(period.listOrder())
    }
}
