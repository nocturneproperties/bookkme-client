package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.GetAppointmentsForDashboardBusiness
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges

internal class GetAppointmentsForDashboardBusinessImpl(
    private val dataSource: AppointmentDataSource,
    private val observeDashboardBusiness: ObserveDashboardBusinessChanges
) : GetAppointmentsForDashboardBusiness {

    override suspend fun invoke(date: LocalDate): List<Appointment> {
        val business = observeDashboardBusiness().firstOrNull() ?: return emptyList()
        return dataSource.getAppointmentsForDate(business.id, date)
    }

    override fun flow(date: LocalDate): Flow<Result<List<Appointment>>> {
        return observeDashboardBusiness()
            .distinctUntilChanged()
            .mapLatest {
                if (it == null) return@mapLatest Result.success(emptyList())
                runCatching {
                    dataSource.getAppointmentsForDate(it.id, date)
                }
            }
    }
}
