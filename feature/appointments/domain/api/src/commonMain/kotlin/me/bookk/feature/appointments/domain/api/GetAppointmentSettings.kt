package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import kotlin.uuid.Uuid

interface GetAppointmentSettings {
    fun flow(businessId: Uuid): Flow<AppointmentSettings?>
    suspend fun refresh(businessId: Uuid): AppointmentSettings
}