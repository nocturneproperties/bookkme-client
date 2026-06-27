package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentsForBusinessImpl(
    private val dataSource: AppointmentDataSource
) : GetAppointmentsForBusiness {

    override suspend fun invoke(businessId: Uuid, date: LocalDate): List<Appointment> =
        dataSource.getAppointmentsForDate(businessId, date)
            .also { dataSource.saveAppointmentsInDB(it) }
}
