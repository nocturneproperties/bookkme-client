package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.first
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.core.domain.fetch.BusinessDataFetchAction
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges

internal class DashboardBusinessDataFetch(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val businessDataFetchActions: List<BusinessDataFetchAction>,
) : BackgroundDataFetchAction {

    override suspend fun doOnBackgroundDataFetch() {
        val business = observeDashboardBusinessChanges().first() ?: return
        businessDataFetchActions.forEach {
            runCatching { it.doOnBusinessDataFetch(business.id) }
        }
    }
}
