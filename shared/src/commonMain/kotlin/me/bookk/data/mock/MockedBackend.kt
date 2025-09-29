package me.bookk.data.mock

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import me.bookk.core.data.mock.RoutingMock
import org.koin.core.component.KoinComponent

internal object MockedBackend : KoinComponent {

    private val mocks: List<RoutingMock> by lazy {
        getKoin().getAll<RoutingMock>()
    }

    val engine = MockEngine { request ->
        for (mock in mocks) {
            if (mock.canHandle(request.method, request.url)) {
                return@MockEngine mock.handle(this, request)
            }
        }
        respondError(
            HttpStatusCode.InternalServerError,
            "Mock isn't provided for given request:${request.url}"
        )
    }
}