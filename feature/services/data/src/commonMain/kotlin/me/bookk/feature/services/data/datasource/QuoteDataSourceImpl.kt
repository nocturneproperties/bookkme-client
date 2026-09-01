package me.bookk.feature.services.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.services.data.remote.api.ServiceRouting.Api
import me.bookk.feature.services.data.remote.model.QuoteRemote
import me.bookk.feature.services.data.remote.model.QuoteRequestRemote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.datasource.QuoteDataSource
import kotlin.uuid.Uuid

internal class QuoteDataSourceImpl(
    private val httpClient: HttpClient
) : DataSource(), QuoteDataSource {
    override suspend fun createQuote(businessId: Uuid, serviceIds: List<Uuid>): Quote = mapExceptions {
        httpClient.post(Api.Service.Quote(Api.Service(businessId = businessId))) {
            setBody(QuoteRequestRemote(serviceIds = serviceIds))
        }
            .body<QuoteRemote>()
            .toDomain()
    }
}
