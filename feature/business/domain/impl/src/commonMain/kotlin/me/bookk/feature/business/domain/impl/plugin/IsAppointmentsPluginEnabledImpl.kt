package me.bookk.feature.business.domain.impl.plugin

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class IsAppointmentsPluginEnabledImpl(
    private val pluginDataSource: PluginDataSource
) : IsAppointmentsPluginEnabled {

    override fun flow(businessId: Uuid): Flow<Boolean?> =
        pluginDataSource.observeAppointmentPluginAvailability(businessId)

    override suspend fun refresh(businessId: Uuid): Boolean =
        pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId)
            .also { pluginDataSource.saveAppointmentPluginAvailability(businessId, it) }
}
