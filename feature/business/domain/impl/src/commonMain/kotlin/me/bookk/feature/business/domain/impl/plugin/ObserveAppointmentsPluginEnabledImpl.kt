package me.bookk.feature.business.domain.impl.plugin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import me.bookk.feature.business.domain.api.entity.BusinessEvent
import me.bookk.feature.business.domain.api.entity.businessEvents
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class ObserveAppointmentsPluginEnabledImpl(
    private val pluginDataSource: PluginDataSource
) : ObserveAppointmentsPluginEnabled {

    override fun invoke(businessId: Uuid): Flow<Boolean> {
        return businessEvents
            .filterIsInstance<BusinessEvent.PluginStateChanged>()
            .onStart { emit(BusinessEvent.PluginStateChanged) }
            .flatMapLatest {
                flow {
                    pluginDataSource.getAppointmentPluginAvailability(businessId)?.let { emit(it) }
                    val availableOnRemote = pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId)
                    pluginDataSource.saveAppointmentPluginAvailability(businessId, availableOnRemote)
                    emit(availableOnRemote)
                }
            }
    }
}
