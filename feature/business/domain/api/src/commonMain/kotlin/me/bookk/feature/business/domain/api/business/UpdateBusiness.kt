package me.bookk.feature.business.domain.api.business

import me.bookk.feature.business.domain.api.entity.Business

interface UpdateBusiness {
    suspend operator fun invoke(model: Business.Update): Business

    sealed interface Error {
        class InvalidCurrency :
            Exception(),
            Error
        class CurrencyLocked :
            Exception(),
            Error
        class InvalidRetentionPeriod :
            Exception(),
            Error
        class ActiveDayWithoutWorkHours :
            Throwable(),
            Error
        class InvalidDayOffRange :
            Throwable(),
            Error
    }
}
