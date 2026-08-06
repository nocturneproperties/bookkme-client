package me.bookk.feature.business.domain.api.plugin

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ObserveAppointmentsPluginEnabled {
    operator fun invoke(businessId: Uuid): Flow<Boolean>
}
