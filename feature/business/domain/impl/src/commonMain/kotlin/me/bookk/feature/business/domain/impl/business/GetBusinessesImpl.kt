package me.bookk.feature.business.domain.impl.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class GetBusinessesImpl(
    private val dataSource: BusinessDataSource,
) : GetBusinesses {

    override fun flow(ids: Set<Uuid>): Flow<List<Business>> {
        return dataSource.observeBusinessesInDb(ids)
    }

    override suspend fun refresh(ids: Set<Uuid>): List<Business> {
        if (ids.isEmpty()) return emptyList()
        return dataSource.getBusinessesByIdsFromRemote(ids).also {
            dataSource.saveBusinessListInDB(it)
        }
    }
}
