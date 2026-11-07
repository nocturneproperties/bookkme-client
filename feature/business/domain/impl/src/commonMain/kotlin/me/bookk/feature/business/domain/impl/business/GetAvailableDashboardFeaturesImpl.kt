package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.DashboardOverview
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled

internal class GetAvailableDashboardFeaturesImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val userProfileCRUD: UserProfileCRUD,
) : GetAvailableDashboardFeatures {

    override fun invoke(): Flow<DashboardOverview?> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business ->
                combine(
                    isAppointmentsPluginEnabled.flow(business.id),
                    userProfileCRUD.observe(),
                ) { isAppointmentsEnabled, profile ->
                    DashboardOverview(
                        business = business,
                        features = business.features(isAppointmentsEnabled == true, profile),
                    )
                }
            }
    }

    private fun Business.features(
        isAppointmentsEnabled: Boolean,
        profile: UserProfile?,
    ): Set<DashboardFeature> {
        return buildSet {
            if (permissions.business.view) add(DashboardFeature.BUSINESS)
            if (permissions.employees.view) add(DashboardFeature.EMPLOYEES)
            if (permissions.clients.view) add(DashboardFeature.CLIENTS)
            if (permissions.services.view) add(DashboardFeature.SERVICES)
            if (isAppointmentsEnabled && permissions.appointments.view) {
                add(DashboardFeature.APPOINTMENTS)
            }
            if (ownerId != null && ownerId == profile?.id) add(DashboardFeature.DATA_EXPORT)
        }
    }
}
