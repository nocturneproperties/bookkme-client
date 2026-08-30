package me.bookk.feature.business.domain.impl.plugin

import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class IsAppointmentsPluginEnabledImpl(
    private val pluginDataSource: PluginDataSource
) : IsAppointmentsPluginEnabled {
    override suspend fun invoke(businessId: Uuid): Boolean {
        return pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId)
            .also { pluginDataSource.saveAppointmentPluginAvailability(businessId, it) }
    }

    override suspend fun cached(businessId: Uuid, onResultAvailable: suspend (Boolean) -> Unit) {
        pluginDataSource.getAppointmentPluginAvailability(businessId)?.let { onResultAvailable(it) }

        onResultAvailable(pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId).also {
            pluginDataSource.saveAppointmentPluginAvailability(businessId, it)
        })
    }
}