package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class UpdateBusinessImpl(
    private val businessDataSource: BusinessDataSource
) : UpdateBusiness {
    override suspend fun invoke(model: Business.Update): Business {
        val currentBusiness = requireNotNull(businessDataSource.getBusinessById(model.id))
        val newBusiness = currentBusiness.copy(
            name = model.name,
            description = model.description,
            address = model.address,
            location = model.location,
            currency = model.currency,
            socials = model.socials
        )
        businessDataSource.updateBusiness(newBusiness)
        businessDataSource.saveBusinessInDB(newBusiness)
        return newBusiness
    }
}