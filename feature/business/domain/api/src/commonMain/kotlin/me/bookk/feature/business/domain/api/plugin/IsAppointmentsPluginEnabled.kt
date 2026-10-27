package me.bookk.feature.business.domain.api.plugin

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface IsAppointmentsPluginEnabled {
    fun flow(businessId: Uuid): Flow<Boolean?>
    suspend fun refresh(businessId: Uuid): Boolean
}
