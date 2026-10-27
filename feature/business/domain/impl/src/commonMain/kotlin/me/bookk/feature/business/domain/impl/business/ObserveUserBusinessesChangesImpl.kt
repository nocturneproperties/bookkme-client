package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class ObserveUserBusinessesChangesImpl(
    private val businessDataSource: BusinessDataSource
) : ObserveUserBusinessesChanges {
    override fun invoke(): Flow<List<Business>> {
        return businessDataSource.observeAllBusinessesInDb()
    }
}
