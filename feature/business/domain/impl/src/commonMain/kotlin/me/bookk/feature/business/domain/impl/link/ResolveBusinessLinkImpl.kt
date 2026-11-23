package me.bookk.feature.business.domain.impl.link

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes

internal class ResolveBusinessLinkImpl(
    private val dataSource: BusinessDataSource,
) : ResolveBusinessLink {

    override suspend fun invoke(name: String): BusinessLink {
        if (!BusinessLink.isValidName(name)) throw ResolveBusinessLink.Error.NotFound()
        return runCatching { dataSource.resolveLink(BusinessLink.normalizeName(name)) }
            .onBusinessError {
                when (it.errorCode) {
                    BusinessErrorCodes.BUSINESS_LINK_NOT_FOUND -> {
                        throw ResolveBusinessLink.Error.NotFound()
                    }
                }
            }
            .getOrThrow()
    }
}
