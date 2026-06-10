package me.bookk.feature.business.domain.datasource

import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

interface PluginDataSource {
    suspend fun enableAppointmentsPlugin(business: Business)
    suspend fun isAppointmentPluginAvailable(businessId: Uuid): Boolean
}