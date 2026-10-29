package me.bookk.feature.business.domain.api.entity

import kotlin.uuid.Uuid

sealed interface DashboardSetupStatus {
    data object NoBusiness : DashboardSetupStatus
    data class BusinessNotSelected(val businesses: List<Business>) : DashboardSetupStatus
    data class SetupRequired(
        val businessId: Uuid,
        val isAppointmentsEnabled: Boolean,
        val hasServiceGroups: Boolean,
        val hasServices: Boolean,
        val hasClients: Boolean,
        val canEnableAppointments: Boolean,
        val canManageServices: Boolean,
        val canManageClients: Boolean,
        val missingViewPermissions: Set<BusinessResource>
    ) : DashboardSetupStatus
    data class AwaitingSetup(
        val businessId: Uuid,
        val businessName: String,
        val missingViewPermissions: Set<BusinessResource>
    ) : DashboardSetupStatus
    data object Ready : DashboardSetupStatus
}
