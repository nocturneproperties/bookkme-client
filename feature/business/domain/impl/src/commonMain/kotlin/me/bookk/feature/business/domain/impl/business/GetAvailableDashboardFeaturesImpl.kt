package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.core.coroutine.mapOrNull
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.DashboardOverview
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled

internal class GetAvailableDashboardFeaturesImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val observeAppointmentsPluginEnabled: ObserveAppointmentsPluginEnabled
) : GetAvailableDashboardFeatures {

    override fun invoke(): Flow<DashboardOverview?> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business ->
                observeAppointmentsPluginEnabled(business.id)
                    .map { business to it }
            }
            .mapOrNull { (business, isAppointmentsEnabled) ->
                DashboardOverview(
                    business = business,
                    features = buildSet {
                        val permissions = business.permissions
                        if (permissions.business.view) add(DashboardFeature.BUSINESS)
                        if (permissions.employees.view) add(DashboardFeature.EMPLOYEES)
                        if (permissions.clients.view) add(DashboardFeature.CLIENTS)
                        if (permissions.services.view) add(DashboardFeature.SERVICES)
                        if (isAppointmentsEnabled && permissions.appointments.view) {
                            add(DashboardFeature.APPOINTMENTS)
                        }
                    }
                )
            }
    }
}
