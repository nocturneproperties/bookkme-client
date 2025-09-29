package me.bookk.core.data.mock

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpMethod

interface MockRequestHandler {
    val method: HttpMethod
    val path: String

    fun canHandle(method: HttpMethod, path: String): Boolean {
        return method == this.method && path.contains(this.path)
    }

    suspend fun handle(scope: MockRequestHandleScope, request: HttpRequestData): HttpResponseData
}