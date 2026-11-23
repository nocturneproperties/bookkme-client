package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BusinessLinkDao
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toEntity
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessLinkRemote
import me.bookk.feature.business.data.remote.model.BusinessLinkRequest
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import kotlin.uuid.Uuid

internal class CommonBusinessLinkDataSource(
    private val httpClient: HttpClient,
    private val businessLinkDao: BusinessLinkDao,
) : DataSource(),
    BusinessLinkDataSource,
    LogOutAction {

    override suspend fun getLink(businessId: Uuid): BusinessLink = mapExceptions {
        httpClient.get(linkRoute(businessId))
            .body<BusinessLinkRemote>()
            .toDomain()
    }

    override suspend fun setLink(businessId: Uuid, name: String): BusinessLink = mapExceptions {
        httpClient.put(linkRoute(businessId)) { setBody(BusinessLinkRequest(name)) }
            .body<BusinessLinkRemote>()
            .toDomain()
    }

    override fun observeLink(businessId: Uuid): Flow<BusinessLink?> {
        return businessLinkDao.observe(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun saveLinkInDb(link: BusinessLink) = mapExceptions {
        businessLinkDao.upsert(link.toEntity())
    }

    override suspend fun deleteLinkFromDb(businessId: Uuid) = mapExceptions {
        businessLinkDao.delete(businessId)
    }

    override suspend fun doOnLogOut() {
        businessLinkDao.clear()
    }

    private fun linkRoute(businessId: Uuid): BusinessRouting.Api.Business.Id.Link {
        return BusinessRouting.Api.Business.Id.Link(
            BusinessRouting.Api.Business.Id(id = businessId),
        )
    }
}
