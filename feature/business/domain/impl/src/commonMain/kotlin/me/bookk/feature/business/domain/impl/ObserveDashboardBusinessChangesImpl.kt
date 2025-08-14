package me.bookk.feature.business.domain.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import me.bookk.feature.business.domain.api.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class ObserveDashboardBusinessChangesImpl(
    private val businessDataSource: BusinessDataSource
) : ObserveDashboardBusinessChanges {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Business?> {
        return businessDataSource.getDashboardBusinessIdFlow()
            .flatMapLatest { businessDataSource.observeBusinessDBChanges(it) }
    }
}