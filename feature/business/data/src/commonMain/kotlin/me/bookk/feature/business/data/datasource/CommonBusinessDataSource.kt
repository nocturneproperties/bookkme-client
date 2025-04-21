package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.data.DataSource
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toLocal
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.CreateBusinessRequest
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CommonBusinessDataSource(
    private val httpClient: HttpClient,
    private val businessDao: BusinessDao
) : DataSource(), BusinessDataSource {
    override suspend fun createBusiness(name: String): Business = mapExceptions {
        httpClient.post(BusinessRouting.Api.Business()) {
            setBody(CreateBusinessRequest(name))
        }
            .body<BusinessRemote>()
            .toDomain()
    }

    override suspend fun saveBusinessInDB(business: Business) {
        mapExceptions { businessDao.insertBusiness(business.toLocal()) }
    }

    override fun observeBusinessDBChanges(): Flow<Business?> {
        return businessDao.observeBusiness()
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun getBusinessFromRemote(): Business = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business())
            .body<BusinessRemote>()
            .toDomain()
    }
}