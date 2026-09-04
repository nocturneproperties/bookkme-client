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
import me.bookk.feature.business.data.remote.model.BusinessPermissionsRemote
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.BusinessUpdateRemote
import me.bookk.feature.business.data.remote.model.ResourcePermissionRemote
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.data.remote.model.toRemote
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
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
    ),
    schedule = WorkingSchedule().toRemote(),
    permissions = mockOwnerPermissions()
)

private fun mockOwnerPermissions(): BusinessPermissionsRemote {
    val fullAccess = ResourcePermissionRemote(view = true, update = true, delete = true)
    return BusinessPermissionsRemote(
        business = fullAccess,
        employees = fullAccess,
        clients = fullAccess,
        services = fullAccess,
        appointments = fullAccess
    )
}

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
        val business = dataSerializer.decodeFromByteArray<BusinessUpdateRemote>(request.body.toByteArray())
        mockBusiness = BusinessRemote(
            id = business.id,
            name = business.name,
            description = business.description,
            address = business.address,
            timeZone = business.timeZone,
            location = business.location,
            currencyCode = business.currencyCode,
            socials = business.socials,
            schedule = business.schedule,
            permissions = mockBusiness.permissions
        )
        return scope.respondOk()
    }
}