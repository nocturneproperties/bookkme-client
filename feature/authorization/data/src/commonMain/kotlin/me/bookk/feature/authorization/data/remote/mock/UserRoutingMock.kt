package me.bookk.feature.authorization.data.remote.mock

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import kotlinx.serialization.encodeToByteArray
import me.bookk.core.data.dataSerializer
import me.bookk.core.data.mock.MockRequestHandler
import me.bookk.core.data.mock.RoutingMock
import me.bookk.feature.authorization.data.remote.api.UserRouting
import me.bookk.feature.authorization.data.remote.model.UserProfileRemote
import kotlin.uuid.Uuid

private var currentUser = UserProfileRemote(
    id = Uuid.random(),
    firstName = "Mock",
    lastName = "User",
    email = "mock.user@email.com"
)

internal class UserRoutingMock : RoutingMock {
    override val handlers = setOf<MockRequestHandler>(
        GetUserHandler()
    ).groupBy { RoutingMock.RoutingKey(it.method, it.path) }
}

private class GetUserHandler : MockRequestHandler {
    override val method: HttpMethod = HttpMethod.Get
    override val path: String = href(ResourcesFormat(), UserRouting.Api.User.Me())

    override suspend fun handle(scope: MockRequestHandleScope, request: HttpRequestData): HttpResponseData {
        return scope.respond(
            content = dataSerializer.encodeToByteArray(currentUser),
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.ProtoBuf.toString()
            ),
        )
    }

}