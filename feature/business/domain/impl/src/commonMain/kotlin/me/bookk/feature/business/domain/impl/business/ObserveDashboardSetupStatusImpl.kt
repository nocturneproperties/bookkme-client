package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled

internal class ObserveDashboardSetupStatusImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled
) : ObserveDashboardSetupStatus {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<DashboardSetupStatus> {
        return observeDashboardBusinessChanges()
            .flatMapLatest { business ->
                if (business == null) {
                    flowOf(DashboardSetupStatus.NoBusiness)
                } else {
                    isAppointmentsPluginEnabled.flow(business.id)
                        .map { isEnabled -> business.setupStatus(isEnabled == true) }
                }
            }
    }

    private fun Business.setupStatus(isAppointmentsEnabled: Boolean): DashboardSetupStatus {
        return when {
            isAppointmentsEnabled -> DashboardSetupStatus.Ready
            permissions.business.update -> DashboardSetupStatus.SetupRequired(id)
            else -> DashboardSetupStatus.AwaitingSetup(name)
        }
    }
}
