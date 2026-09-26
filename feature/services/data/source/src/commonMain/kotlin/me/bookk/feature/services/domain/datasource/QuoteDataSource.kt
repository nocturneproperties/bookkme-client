package me.bookk.feature.services.domain.datasource

import me.bookk.feature.services.domain.api.quote.entity.Quote
import kotlin.uuid.Uuid

interface QuoteDataSource {
    suspend fun createQuote(businessId: Uuid, serviceIds: List<Uuid>): Quote
}
