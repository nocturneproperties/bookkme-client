package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toLocal
import me.bookk.feature.business.data.mapping.toUserBusinesses
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.CreateBusinessRequest
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CommonBusinessDataSource(
    private val httpClient: HttpClient,
    private val businessDao: BusinessDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), BusinessDataSource {

    private val preferences = preferenceProvider.get("business_prefs")

    override suspend fun createBusiness(name: String, currencyCode: String): Business =
        mapExceptions {
            httpClient.post(BusinessRouting.Api.Business()) {
                setBody(CreateBusinessRequest(name, currencyCode))
            }
                .body<BusinessRemote>()
                .toDomain()
        }

    override suspend fun clearBusinessTable() = mapExceptions {
        businessDao.clear()
    }

    override suspend fun saveBusinessInDB(business: Business) {
        mapExceptions { businessDao.upsertBusiness(business.toLocal()) }
    }

    override suspend fun saveBusinessListInDB(businesses: List<Business>) {
        mapExceptions { businessDao.upsertBusiness(businesses.map { it.toLocal() }) }
    }

    override fun observeBusinessDBChanges(businessId: Long): Flow<Business?> {
        return businessDao.observeBusiness(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun getBusinessesFromRemote(): UserBusinessInfo = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business())
            .body<UserBusinessesRemote>()
            .toUserBusinesses()
    }

    override suspend fun saveDashboardBusinessId(id: Long) {
        preferences.set(Key.dashboardId, id)
    }

    override suspend fun getDashboardBusinessId(): Long {
        return preferences.get(Key.dashboardId) ?: -1
    }

    override fun getDashboardBusinessIdFlow(): Flow<Long> {
        return preferences.getFlow(Key.dashboardId).map { it ?: -1 }
    }

    private object Key {
        val dashboardId = Preferences.Key<Long>("dashboard_id")
    }
}