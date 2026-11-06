package me.bookk.feature.appointments.domain.datasource

import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled

interface BusinessAppointmentsDataSource {
    suspend fun getEnabled(): List<BusinessAppointmentsEnabled>
}
