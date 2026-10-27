package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessIdChanges
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class ObserveDashboardBusinessIdChangesImpl(
    private val businessDataSource: BusinessDataSource
) : ObserveDashboardBusinessIdChanges {

    override fun invoke(): Flow<Uuid?> {
        return businessDataSource.getDashboardBusinessIdFlow()
    }
}
