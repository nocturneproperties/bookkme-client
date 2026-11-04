package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetBusinessAppointmentsEnabled
import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled
import me.bookk.feature.appointments.domain.datasource.BusinessAppointmentsDataSource

internal class GetBusinessAppointmentsEnabledImpl(
    private val dataSource: BusinessAppointmentsDataSource,
) : GetBusinessAppointmentsEnabled {
    override suspend fun invoke(): List<BusinessAppointmentsEnabled> {
        return dataSource.getEnabled()
    }
}
