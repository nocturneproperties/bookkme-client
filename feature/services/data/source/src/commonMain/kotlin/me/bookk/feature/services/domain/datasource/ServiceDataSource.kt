package me.bookk.feature.services.domain.datasource

import kotlinx.coroutines.flow.Flow
import library.money.api.Money
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface ServiceDataSource {
    suspend fun getServices(businessId: Uuid): List<Service>
    suspend fun createService(service: Service): Service
    suspend fun editService(service: Service): Service
    suspend fun deleteService(businessId: Uuid, id: Uuid)
    suspend fun getBusinessCurrency(businessId: Uuid): Money.SupportedCurrency

    suspend fun saveServicesInDB(services: List<Service>)
    suspend fun saveServiceInDB(service: Service)
    suspend fun deleteServiceFromDB(service: Service)
    fun observeServicesDBChanges(businessId: Uuid): Flow<List<Service>>
    suspend fun getServiceIdsInDb(businessId: Uuid): List<Uuid>
    suspend fun deleteServicesInDb(ids: List<Uuid>)
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
}