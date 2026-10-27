package me.bookk.feature.services.domain.api.quote

import me.bookk.feature.services.domain.api.quote.entity.Quote
import kotlin.uuid.Uuid

interface CreateQuote {
    suspend operator fun invoke(businessId: Uuid, serviceIds: List<Uuid>): Quote

    sealed interface Error {
        class ServiceNotFound(cause: Throwable) : Error, Throwable(cause)
    }
}
