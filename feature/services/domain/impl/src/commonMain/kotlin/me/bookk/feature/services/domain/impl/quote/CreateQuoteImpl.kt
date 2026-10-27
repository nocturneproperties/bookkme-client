package me.bookk.feature.services.domain.impl.quote

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.CreateQuote.Error
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.datasource.QuoteDataSource
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import kotlin.uuid.Uuid

internal class CreateQuoteImpl(
    private val dataSource: QuoteDataSource
) : CreateQuote {
    override suspend fun invoke(businessId: Uuid, serviceIds: List<Uuid>): Quote = runCatching {
        dataSource.createQuote(businessId, serviceIds)
    }.onBusinessError { error ->
        when (error.errorCode) {
            ServiceErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND -> throw Error.ServiceNotFound(error)
        }
    }.getOrThrow()
}
