package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import kotlin.uuid.Uuid

interface GetAppointmentSettings {
    suspend operator fun invoke(businessId: Uuid): AppointmentSettings
}