package me.bookk.feature.business.domain.impl.plugin

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.business.domain.api.entity.BusinessEvent
import me.bookk.feature.business.domain.api.entity.businessEvents
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin.Error
import me.bookk.feature.business.domain.datasource.AppointmentsErrorCodes
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class EnableAppointmentsPluginImpl(
    private val pluginDataSource: PluginDataSource,
) : EnableAppointmentsPlugin {
    override suspend fun invoke(businessId: Uuid) {
        runCatching {
            pluginDataSource.enableAppointmentsPlugin(businessId)
            businessEvents.emit(BusinessEvent.PluginStateChanged)
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentsErrorCodes.PLUGIN_ALREADY_ENABLED -> {
                    businessEvents.emit(BusinessEvent.PluginStateChanged)
                    throw Error.AlreadyEnabled()
                }
            }
        }
    }
}