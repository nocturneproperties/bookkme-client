package me.bookk.feature.services.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import library.money.api.Money
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.ServiceDao
import me.bookk.feature.services.data.mapper.toDb
import me.bookk.feature.services.data.mapper.toDomain
import me.bookk.feature.services.data.remote.api.ServiceRouting.Api
import me.bookk.feature.services.data.remote.model.ServiceRemote
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class ServiceDataSourceImpl(
    private val httpClient: HttpClient,
    private val serviceDao: ServiceDao,
    private val businessDao: BusinessDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), ServiceDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("services_prefs")
    override suspend fun getServices(businessId: Uuid): List<Service> = mapExceptions {
        httpClient.get(Api.Service(businessId = businessId))
            .body<List<ServiceRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun createService(service: Service): Service = mapExceptions {
        httpClient.post(Api.Service(businessId = service.businessId)) {
            setBody(ServiceRemote.fromDomain(service))
        }
            .body<ServiceRemote>()
            .toDomain()
    }

    override suspend fun editService(service: Service): Service = mapExceptions {
        httpClient.put(Api.Service.Id(Api.Service(businessId = service.businessId), service.id)) {
            setBody(ServiceRemote.fromDomain(service))
        }
            .body<ServiceRemote>()
            .toDomain()
    }

    override suspend fun deleteService(businessId: Uuid, id: Uuid) {
        mapExceptions {
            httpClient.delete(Api.Service.Id(Api.Service(businessId = businessId), id))
        }
    }

    override suspend fun getBusinessCurrency(businessId: Uuid): Money.SupportedCurrency {
        return Money.SupportedCurrency.fromCode(
            requireNotNull(businessDao.queryBusiness(businessId)).entity.currencyCode
        )
    }

    override suspend fun saveServicesInDB(services: List<Service>) = mapExceptions {
        serviceDao.upsert(services.map { it.toDb() })
    }

    override suspend fun saveServiceInDB(service: Service) = mapExceptions {
        serviceDao.upsert(service.toDb())
    }

    override suspend fun deleteServiceFromDB(service: Service) = mapExceptions {
        serviceDao.delete(service.toDb())
    }

    override fun observeServicesDBChanges(businessId: Uuid): Flow<List<Service>> {
        return serviceDao.observe(businessId)
            .map { services -> services.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun getServiceIdsInDb(businessId: Uuid): List<Uuid> {
        return mapExceptions { serviceDao.getIds(businessId) }
    }

    override suspend fun deleteServicesInDb(ids: List<Uuid>) {
        mapExceptions {
            ids.chunked(DELETE_CHUNK_SIZE).forEach { chunk -> serviceDao.deleteByIds(chunk) }
        }
    }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        serviceDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }
}