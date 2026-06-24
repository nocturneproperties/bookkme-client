package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toLocal
import me.bookk.feature.business.data.mapping.toRemote
import me.bookk.feature.business.data.mapping.toUserBusinesses
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.CreateBusinessRequest
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class CommonBusinessDataSource(
    private val httpClient: HttpClient,
    private val businessDao: BusinessDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), BusinessDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("business_prefs")

    override suspend fun createBusiness(name: String, currencyCode: String, timeZone: TimeZone): Business =
        mapExceptions {
            httpClient.post(BusinessRouting.Api.Business()) {
                setBody(CreateBusinessRequest(name, currencyCode, timeZone))
            }
                .body<BusinessRemote>()
                .toDomain()
        }

    override suspend fun updateBusiness(business: Business) {
        mapExceptions {
            httpClient.put(BusinessRouting.Api.Business.Id(id = business.id)) {
                setBody(business.toRemote())
            }
        }
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

    override fun observeBusinessDBChanges(businessId: Uuid): Flow<Business?> {
        return businessDao.observeBusiness(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun getBusinessesFromRemote(): UserBusinessInfo = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business())
            .body<UserBusinessesRemote>()
            .toUserBusinesses()
    }

    override suspend fun getBusinessById(id: Uuid): Business? {
        return businessDao.queryBusiness(id)?.toDomain()
    }

    override suspend fun saveDashboardBusinessId(id: Uuid?) {
        preferences.set(Key.dashboardId, id?.toString())
    }

    override suspend fun getDashboardBusinessId(): Uuid? {
        return preferences.get(Key.dashboardId)?.let { Uuid.parse(it) }
    }

    override fun getDashboardBusinessIdFlow(): Flow<Uuid?> {
        return preferences.getFlow(Key.dashboardId)
            .map { it?.let { Uuid.parse(it) } }
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
    }

    private object Key {
        val dashboardId = Preferences.Key<String>("dashboard_id")
    }
}