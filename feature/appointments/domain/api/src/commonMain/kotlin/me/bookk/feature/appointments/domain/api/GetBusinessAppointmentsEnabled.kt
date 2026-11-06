package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled

interface GetBusinessAppointmentsEnabled {
    suspend operator fun invoke(): List<BusinessAppointmentsEnabled>
}
