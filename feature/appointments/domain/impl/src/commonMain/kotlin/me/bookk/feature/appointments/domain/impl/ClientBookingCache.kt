package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.appointments.domain.datasource.DateBounds

internal suspend fun ClientBookingDataSource.replaceAppointments(
    period: ClientBookingPeriod,
    dates: DateBounds,
    appointments: List<Appointment>,
) {
    val freshIds = appointments.map { it.id }.toSet()
    val staleIds = getAppointmentIdsInDb(period, dates).filterNot { it in freshIds }
    if (staleIds.isNotEmpty()) deleteAppointmentsInDb(staleIds)
    saveAppointmentsInDb(appointments)
}

internal suspend fun ClientBookingDataSource.replaceRequests(
    period: ClientBookingPeriod,
    dates: DateBounds,
    requests: List<AppointmentRequest>,
) {
    val freshIds = requests.map { it.id }.toSet()
    val staleIds = getRequestIdsInDb(period, dates).filterNot { it in freshIds }
    if (staleIds.isNotEmpty()) deleteRequestsInDb(staleIds)
    saveRequestsInDb(requests)
}
