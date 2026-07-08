package me.bookk.feature.business.domain.datasource

import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

interface PluginDataSource {
    suspend fun enableAppointmentsPlugin(business: Business)
    suspend fun isAppointmentPluginAvailableOnRemote(businessId: Uuid): Boolean
    suspend fun saveAppointmentPluginAvailability(businessId: Uuid, isAvailable: Boolean)
    suspend fun getAppointmentPluginAvailability(businessId: Uuid): Boolean
}