package me.bookk.feature.business.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.ObserveBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class ObserveBusinessChangesImpl(
    private val businessDataSource: BusinessDataSource
) : ObserveBusinessChanges {
    override fun invoke(): Flow<Business?> {
        return businessDataSource.observeBusinessDBChanges()
    }
}