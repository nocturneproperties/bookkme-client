package me.bookk.core.data.mock

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.fullPath

interface RoutingMock {

    val handlers: Map<RoutingKey, List<MockRequestHandler>>

    fun canHandle(method: HttpMethod, request: Url): Boolean {
        val key = RoutingKey(method, request.fullPath)
        return handlers[key]?.isNotEmpty() ?: false
    }

    suspend fun handle(scope: MockRequestHandleScope, request: HttpRequestData): HttpResponseData {
        val key = RoutingKey(request.method, request.url.fullPath)
        val handler = handlers[key]?.firstOrNull() ?: return scope.respondError(HttpStatusCode.InternalServerError)
        return handler.handle(scope, request)
    }

    data class RoutingKey(
        val method: HttpMethod,
        val path: String
    )
}