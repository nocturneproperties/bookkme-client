package me.bookk.feature.services.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.database.dao.ServiceDao
import me.bookk.feature.services.data.mapper.toDb
import me.bookk.feature.services.data.mapper.toDomain
import me.bookk.feature.services.data.remote.api.ServiceRouting.Api
import me.bookk.feature.services.data.remote.model.ServiceRemote
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import kotlin.uuid.Uuid

internal class ServiceDataSourceImpl(
    private val httpClient: HttpClient,
    private val serviceDao: ServiceDao,
) : DataSource(), ServiceDataSource {
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

    override suspend fun saveServicesInDB(services: List<Service>) = mapExceptions {
        serviceDao.upsert(services.map { it.toDb() })
    }

    override suspend fun saveServiceInDB(service: Service) = mapExceptions {
        serviceDao.upsert(service.toDb())
    }

    override suspend fun deleteServiceFromDB(service: Service) = mapExceptions {
        serviceDao.delete(service.toDb())
    }

    override suspend fun getServicesFromDb(businessId: Uuid): List<Service> {
        return serviceDao.get(businessId).map { it.toDomain() }
    }
}