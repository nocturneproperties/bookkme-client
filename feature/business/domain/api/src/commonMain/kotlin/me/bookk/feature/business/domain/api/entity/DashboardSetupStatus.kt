package me.bookk.feature.business.domain.api.entity

import kotlin.uuid.Uuid

sealed interface DashboardSetupStatus {
    data object NoBusiness : DashboardSetupStatus
    data class SetupRequired(val businessId: Uuid) : DashboardSetupStatus
    data class AwaitingSetup(val businessName: String) : DashboardSetupStatus
    data object Ready : DashboardSetupStatus
}
