package me.bookk.feature.business.domain.impl

import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes

internal class RefreshBusinessInfoImpl(
    private val businessDataSource: BusinessDataSource
) : RefreshBusinessInfo {
    override suspend fun invoke(): Business? {
        val business = runCatching {
            businessDataSource.getBusinessFromRemote()
        }.getOrElse {
            val exception = it.businessOrThrow()
            if (exception.errorCode == BusinessErrorCodes.BUSINESS_NOT_FOUND) {
                null
            } else {
                throw it
            }
        }
        if (business != null) {
            businessDataSource.saveBusinessInDB(business)
        } else {
            businessDataSource.clearBusinessTable()
        }
        return business
    }
}