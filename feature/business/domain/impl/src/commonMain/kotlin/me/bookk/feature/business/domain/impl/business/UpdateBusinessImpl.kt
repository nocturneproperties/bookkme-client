package me.bookk.feature.business.domain.impl.business

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes

internal class UpdateBusinessImpl(private val businessDataSource: BusinessDataSource) :
    UpdateBusiness {
    override suspend fun invoke(model: Business.Update): Business {
        val currentBusiness = requireNotNull(businessDataSource.getBusinessById(model.id))
        val newBusiness = currentBusiness.copy(
            name = model.name,
            description = model.description,
            address = model.address,
            location = model.location,
            currency = model.currency,
            socials = model.socials,
            schedule = model.schedule,
            clientDataRetentionMonths =
            model.clientDataRetentionMonths ?: currentBusiness.clientDataRetentionMonths,
        )
        return runCatching {
            businessDataSource.updateBusiness(newBusiness)
            businessDataSource.saveBusinessInDB(newBusiness)
            newBusiness
        }
            .onBusinessError {
                when (it.errorCode) {
                    BusinessErrorCodes.BUSINESS_INVALID_CURRENCY ->
                        throw UpdateBusiness.Error.InvalidCurrency()

                    BusinessErrorCodes.BUSINESS_CURRENCY_LOCKED ->
                        throw UpdateBusiness.Error.CurrencyLocked()

                    BusinessErrorCodes.BUSINESS_CLIENT_DATA_RETENTION_OUT_OF_RANGE ->
                        throw UpdateBusiness.Error.InvalidRetentionPeriod()

                    BusinessErrorCodes.BUSINESS_ACTIVE_DAY_WITHOUT_WORK_HOURS ->
                        throw UpdateBusiness.Error.ActiveDayWithoutWorkHours()

                    BusinessErrorCodes.BUSINESS_INVALID_DAY_OFF_RANGE ->
                        throw UpdateBusiness.Error.InvalidDayOffRange()
                }
            }
            .getOrThrow()
    }
}
