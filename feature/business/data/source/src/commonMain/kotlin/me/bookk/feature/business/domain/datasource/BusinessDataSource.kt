package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo

interface BusinessDataSource {
    suspend fun createBusiness(name: String, currencyCode: String): Business
    suspend fun clearBusinessTable()
    suspend fun saveBusinessInDB(business: Business)
    suspend fun saveBusinessListInDB(businesses: List<Business>)
    suspend fun getBusinessesFromRemote(): UserBusinessInfo
    suspend fun saveDashboardBusinessId(id: Long)
    suspend fun getDashboardBusinessId(): Long
    fun getDashboardBusinessIdFlow(): Flow<Long>
    fun observeBusinessDBChanges(businessId: Long): Flow<Business?>
}