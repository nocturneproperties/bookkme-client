package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.GetServices

internal class ObserveDashboardSetupStatusImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val getServiceGroups: GetServiceGroups,
    private val getServices: GetServices,
    private val getClientsList: GetClientsList
) : ObserveDashboardSetupStatus {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<DashboardSetupStatus> {
        return observeDashboardBusinessChanges()
            .flatMapLatest { business -> business?.setupStatus() ?: unselectedBusinessStatus() }
    }

    private fun unselectedBusinessStatus(): Flow<DashboardSetupStatus> {
        return observeUserBusinessesChanges().map { businesses ->
            if (businesses.isEmpty()) {
                DashboardSetupStatus.NoBusiness
            } else {
                DashboardSetupStatus.BusinessNotSelected(businesses)
            }
        }
    }

    private fun Business.setupStatus(): Flow<DashboardSetupStatus> {
        return combine(
            isAppointmentsPluginEnabled.flow(id),
            getServiceGroups.flow(),
            getServices.flow(),
            getClientsList.flow()
        ) { isEnabled, groups, services, clients ->
            val setup = DashboardSetupStatus.SetupRequired(
                businessId = id,
                isAppointmentsEnabled = isEnabled == true,
                hasServiceGroups = groups.isNotEmpty(),
                hasServices = services.isNotEmpty(),
                hasClients = clients.isNotEmpty(),
                canEnableAppointments = permissions.business.update,
                canManageServices = permissions.services.view && permissions.services.update,
                canManageClients = permissions.clients.view && permissions.clients.update,
                missingViewPermissions = missingViewPermissions()
            )
            when {
                setup.isComplete() -> DashboardSetupStatus.Ready
                setup.hasStepUserCanComplete() -> setup
                else -> DashboardSetupStatus.AwaitingSetup(id, name, setup.missingViewPermissions)
            }
        }
    }

    private fun Business.missingViewPermissions(): Set<BusinessResource> {
        return buildSet {
            if (!permissions.services.view) add(BusinessResource.SERVICES)
            if (!permissions.clients.view) add(BusinessResource.CLIENTS)
        }
    }

    private fun DashboardSetupStatus.SetupRequired.isComplete(): Boolean {
        return isAppointmentsEnabled && hasServiceGroups && hasServices && hasClients
    }

    private fun DashboardSetupStatus.SetupRequired.hasStepUserCanComplete(): Boolean {
        val canEnablePlugin = canEnableAppointments && !isAppointmentsEnabled
        val canAddServices = canManageServices && !(hasServiceGroups && hasServices)
        val canAddClient = canManageClients && !hasClients
        return canEnablePlugin || canAddServices || canAddClient
    }
}
