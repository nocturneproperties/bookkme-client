package me.bookk.feature.business.domain.api.plugin

import kotlin.uuid.Uuid

interface IsAppointmentsPluginEnabled {
    suspend operator fun invoke(businessId: Uuid): Boolean
    suspend fun cached(businessId: Uuid, onResultAvailable: suspend (Boolean) -> Unit)
}