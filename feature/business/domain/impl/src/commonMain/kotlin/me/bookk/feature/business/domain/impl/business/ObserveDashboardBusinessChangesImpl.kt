package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class ObserveDashboardBusinessChangesImpl(
    private val businessDataSource: BusinessDataSource
) : ObserveDashboardBusinessChanges {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Business?> {
        return businessDataSource.getDashboardBusinessIdFlow()
            .flatMapLatest { id ->
                if (id != null) {
                    businessDataSource.observeBusinessDBChanges(id)
                } else {
                    flowOf(null)
                }
            }
    }
}