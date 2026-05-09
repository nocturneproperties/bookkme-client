package me.bookk.feature.business.domain.impl.business

import me.bookk.core.domain.entity.Error
import me.bookk.feature.business.domain.api.business.GetBusinessById
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class GetBusinessByIdImpl(
    private val businessDataSource: BusinessDataSource
) : GetBusinessById {
    override suspend fun invoke(id: Uuid): Business {
        return businessDataSource.getBusinessById(id) ?: throw Error.InvalidApplicationState
    }
}