package me.bookk.core.data.map

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import me.bookk.core.data.BusinessServerError
import me.bookk.core.data.ServerErrorCodes
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MapperTest {

    private suspend fun failureFor(status: HttpStatusCode, errorCode: Int, message: String = "error"): Throwable {
        val body = dataSerializer.encodeToByteArray(BusinessServerError.serializer(), BusinessServerError(errorCode, message))
        val client = HttpClient(MockEngine { respond(body, status, headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString())) }) {
            expectSuccess = true
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        return runCatching { client.get("https://bookk.test") }.exceptionOrNull()!!
    }

    @Test
    fun `maps forbidden access suspended response to business access suspended`() = runUnitTest {
        given()
        val failure = failureFor(HttpStatusCode.Forbidden, ServerErrorCodes.BUSINESS_EMPLOYEE_ACCESS_SUSPENDED, "suspended")

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.BusinessAccessSuspended>(result)
        assertEquals("suspended", result.message)
    }

    @Test
    fun `maps forbidden response with another code to business error`() = runUnitTest {
        given()
        val failure = failureFor(HttpStatusCode.Forbidden, 200027)

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.BusinessError>(result)
        assertEquals(200027, result.errorCode)
    }

    @Test
    fun `maps access suspended code with non forbidden status to business error`() = runUnitTest {
        given()
        val failure = failureFor(HttpStatusCode.UnprocessableEntity, ServerErrorCodes.BUSINESS_EMPLOYEE_ACCESS_SUSPENDED)

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.BusinessError>(result)
    }
}
