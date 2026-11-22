package me.bookk.feature.business.domain.impl.link

import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink.Error
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class SetBusinessLinkImpl(
    private val dataSource: BusinessLinkDataSource,
) : SetBusinessLink {
    override suspend fun invoke(businessId: Uuid, name: String): BusinessLink {
        val linkName = BusinessLink.normalizeName(name)
        if (!BusinessLink.isValidName(linkName)) throw Error.Invalid()
        return runCatching { dataSource.setLink(businessId, linkName) }
            .getOrElse { throw it.toSetLinkError() }
            .also { dataSource.saveLinkInDb(it) }
    }

    private fun Throwable.toSetLinkError(): Throwable {
        return when {
            this is DomainError.NotFound -> Error.NotOwner()
            this !is DomainError.BusinessError -> this
            else -> errorCode.toSetLinkError() ?: this
        }
    }

    private fun Int.toSetLinkError(): Throwable? {
        return when (this) {
            BusinessErrorCodes.BUSINESS_LINK_INVALID -> Error.Invalid()
            BusinessErrorCodes.BUSINESS_LINK_RESERVED -> Error.Reserved()
            BusinessErrorCodes.BUSINESS_LINK_TAKEN -> Error.Taken()
            BusinessErrorCodes.BUSINESS_LINK_CHANGE_LIMIT_REACHED -> Error.ChangeLimitReached()
            else -> null
        }
    }
}
