package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import kotlin.uuid.Uuid

interface BusinessDataSource {
    suspend fun createBusiness(name: String, currencyCode: String, timeZone: TimeZone): Business
    suspend fun updateBusiness(business: Business)
    suspend fun clearBusinessTable()
    suspend fun saveBusinessInDB(business: Business)
    suspend fun saveBusinessListInDB(businesses: List<Business>)
    suspend fun getBusinessesFromRemote(): UserBusinessInfo
    suspend fun getBusinessById(id: Uuid): Business?
    suspend fun saveDashboardBusinessId(id: Uuid?)
    suspend fun getDashboardBusinessId(): Uuid?
    fun getDashboardBusinessIdFlow(): Flow<Uuid?>
    fun observeBusinessDBChanges(businessId: Uuid): Flow<Business?>
    suspend fun saveDashboardFeatures(businessId: Uuid, features: Set<DashboardFeature>)
    suspend fun getDashboardFeatures(businessId: Uuid): Set<DashboardFeature>?
}