package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface PluginDataSource {
    suspend fun enableAppointmentsPlugin(businessId: Uuid)
    suspend fun isAppointmentPluginAvailableOnRemote(businessId: Uuid): Boolean
    suspend fun saveAppointmentPluginAvailability(businessId: Uuid, isAvailable: Boolean)
    suspend fun getAppointmentPluginAvailability(businessId: Uuid): Boolean?
    fun observeAppointmentPluginAvailability(businessId: Uuid): Flow<Boolean?>
}