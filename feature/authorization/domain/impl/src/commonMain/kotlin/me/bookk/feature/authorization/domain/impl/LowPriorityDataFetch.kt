package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationToken

internal open class LowPriorityDataFetch(
    private val applicationScope: CoroutineScope,
    private val updateNotificationToken: UpdateNotificationToken,
    private val getServices: GetServices,
    private val getServiceGroups: GetServiceGroups,
    private val getClientsList: GetClientsList,
    private val getEmployees: GetEmployees,
    private val getNotificationSettings: GetNotificationSettings,
    private val getAppointmentEnabled: IsAppointmentsPluginEnabled,
    private val getAppointmentSettings: GetAppointmentSettings,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) {

    // 1. Calling this requests sequentially is intentional, to not spam the server
    // 2. Grouping done assuming backend services architecture. Entities stored at the same logical entry (service) will likely fail together
    open operator fun invoke() = applicationScope.launch {
        val business = observeDashboardBusinessChanges().filterNotNull().firstOrNull() ?: return@launch

        runCatching {
            getServices(business.id)
            getServiceGroups(business.id)
            getClientsList.refresh(business.id)
            getEmployees(business.id)
            getAppointmentEnabled(business.id)
        }
        runCatching { getAppointmentSettings(business.id) }
        runCatching {
            updateNotificationToken()
            getNotificationSettings()
        }

    }
}