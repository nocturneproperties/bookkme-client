package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business

interface BusinessDataSource {
    suspend fun createBusiness(name: String): Business
    suspend fun saveBusinessInDB(business: Business)
    suspend fun getBusinessFromRemote(): Business?
    fun observeBusinessDBChanges(): Flow<Business?>
}