package me.bookk.feature.services.domain.datasource

import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

interface ServiceDataSource {
    suspend fun getServices(businessId: Uuid): List<Service>
    suspend fun createService(service: Service): Service
    suspend fun editService(service: Service): Service
    suspend fun deleteService(businessId: Uuid, id: Uuid)

    suspend fun saveServicesInDB(services: List<Service>)
    suspend fun saveServiceInDB(service: Service)
    suspend fun deleteServiceFromDB(service: Service)
    suspend fun getServicesFromDb(businessId: Uuid): List<Service>
}