package me.bookk.feature.business.domain.impl.plugin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class ObserveAppointmentsPluginEnabledImpl(
    private val pluginDataSource: PluginDataSource
) : ObserveAppointmentsPluginEnabled {

    override fun invoke(businessId: Uuid): Flow<Boolean> {
        return pluginDataSource.observeAppointmentPluginAvailability(businessId)
            .map { it ?: false }
    }
}
