package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.RefreshDashboardSetupStatus
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.uuid.Uuid

internal class RefreshDashboardSetupStatusImpl(
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val getServiceGroups: GetServiceGroups,
    private val getServices: GetServices,
    private val getClientsList: GetClientsList
) : RefreshDashboardSetupStatus {

    override suspend fun invoke(businessId: Uuid) {
        val failure = listOf(
            runCatching { isAppointmentsPluginEnabled.refresh(businessId) },
            runCatching { getServiceGroups.refresh(businessId) },
            runCatching { getServices.refresh(businessId) },
            runCatching { getClientsList.refresh(businessId) }
        ).firstNotNullOfOrNull { it.exceptionOrNull() }
        failure?.let { throw it }
    }
}
