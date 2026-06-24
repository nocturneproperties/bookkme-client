package me.bookk.feature.business.data.remote.mock

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import kotlinx.datetime.TimeZone
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import me.bookk.core.data.dataSerializer
import me.bookk.core.data.mock.MockRequestHandler
import me.bookk.core.data.mock.RoutingMock
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import kotlin.uuid.Uuid

internal class BusinessRoutingMock : RoutingMock {
    override val handlers = setOf(
        GetBusinessesHandler(),
        UpdateBusinessesHandler()
    ).groupBy { RoutingMock.RoutingKey(it.method, it.path) }
}

private var mockBusiness = BusinessRemote(
    id = Uuid.random(),
    name = "Mock business",
    description = "Short description for mocked business",
    address = "Test Address",
    location = BusinessRemote.Location(20.0, 16.0),
    currencyCode = "UAH",
    timeZone = TimeZone.currentSystemDefault(),
    socials = listOf(
        BusinessRemote.Social(BusinessRemote.SocialKind.VIBER, "viber"),
        BusinessRemote.Social(BusinessRemote.SocialKind.INSTAGRAM, "insta"),
        BusinessRemote.Social(BusinessRemote.SocialKind.TELEGRAM, "telegram")
    )
)

private class GetBusinessesHandler : MockRequestHandler {
    override val method: HttpMethod = HttpMethod.Get
    override val path: String = href(ResourcesFormat(), BusinessRouting.Api.Business())

    override suspend fun handle(
        scope: MockRequestHandleScope,
        request: HttpRequestData
    ): HttpResponseData {
        val businesses = UserBusinessesRemote(mockBusiness.id, listOf(mockBusiness))
        return scope.respond(
            content = dataSerializer.encodeToByteArray(businesses),
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.ProtoBuf.toString()
            ),
        )
    }
}

private class UpdateBusinessesHandler : MockRequestHandler {
    override val method: HttpMethod = HttpMethod.Put
    override val path: String
        get() = href(ResourcesFormat(), BusinessRouting.Api.Business.Id(id = mockBusiness.id))

    override suspend fun handle(
        scope: MockRequestHandleScope,
        request: HttpRequestData
    ): HttpResponseData {
        val business = dataSerializer.decodeFromByteArray<BusinessRemote>(request.body.toByteArray())
        mockBusiness = business
        return scope.respondOk()
    }
}