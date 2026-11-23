package me.bookk.feature.business.domain.impl.link

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.GetBusinessLink
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class GetBusinessLinkImpl(
    private val dataSource: BusinessLinkDataSource,
) : GetBusinessLink {
    override fun flow(businessId: Uuid): Flow<BusinessLink?> {
        return dataSource.observeLink(businessId)
    }

    override suspend fun refresh(businessId: Uuid) {
        val link = fetchLink(businessId)
        if (link == null) {
            dataSource.deleteLinkFromDb(businessId)
        } else {
            dataSource.saveLinkInDb(link)
        }
    }

    private suspend fun fetchLink(businessId: Uuid): BusinessLink? {
        return runCatching { dataSource.getLink(businessId) }
            .getOrElse { error ->
                when {
                    error.isLinkNotFound() -> null
                    error is DomainError.NotFound -> forgetLink(businessId)
                    else -> throw error
                }
            }
    }

    private suspend fun forgetLink(businessId: Uuid): Nothing {
        dataSource.deleteLinkFromDb(businessId)
        throw GetBusinessLink.Error.NotOwner()
    }

    private fun Throwable.isLinkNotFound(): Boolean {
        return this is DomainError.BusinessError &&
            errorCode == BusinessErrorCodes.BUSINESS_LINK_NOT_FOUND
    }
}
