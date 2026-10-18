package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentsForBusinessImpl(
    private val dataSource: AppointmentDataSource,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId
) : GetAppointmentsForBusiness {

    override fun flow(date: LocalDate, employeeId: Uuid?): Flow<List<Appointment>> {
        return observeCurrentBusinessId()
            .flatMapLatestOrNull { businessId ->
                dataSource.observeAppointmentsForDateDBChanges(businessId, date, employeeId)
            }
            .map { it.orEmpty() }
    }

    override suspend fun refresh(businessId: Uuid, date: LocalDate, employeeId: Uuid?): List<Appointment> {
        val appointments = dataSource.getAppointmentsForDate(businessId, date, employeeId)
        val freshIds = appointments.map { it.id }.toSet()
        val staleIds = dataSource.getAppointmentIdsForDateInDb(businessId, date, employeeId)
            .filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteAppointmentsInDb(staleIds)
        }
        dataSource.saveAppointmentsInDB(appointments)
        if (employeeId == null) {
            dataSource.saveLastSyncedAt(businessId, date)
        }
        return appointments
    }
}
