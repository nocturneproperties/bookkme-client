package me.bookk.feature.business.domain.api.business

import me.bookk.feature.business.domain.api.entity.Business

interface UpdateBusiness {
    suspend operator fun invoke(model: Business.Update): Business

    sealed interface Error {
        class ActiveDayWithoutWorkHours : Throwable(), Error
        class InvalidDayOffRange : Throwable(), Error
    }
}