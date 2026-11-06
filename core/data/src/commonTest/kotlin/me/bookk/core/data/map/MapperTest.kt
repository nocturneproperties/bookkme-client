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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.data.BusinessServerError
import me.bookk.core.data.ServerErrorCodes
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class MapperTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun failureFor(
        status: HttpStatusCode,
        errorCode: Int,
        message: String = "error",
    ): Throwable {
        val body = dataSerializer.encodeToByteArray(
            BusinessServerError.serializer(),
            BusinessServerError(errorCode, message),
        )
        return failureFor(status, body, ContentType.Application.ProtoBuf)
    }

    private suspend fun failureFor(
        status: HttpStatusCode,
        body: ByteArray,
        contentType: ContentType,
    ): Throwable {
        val client = HttpClient(
            MockEngine {
                respond(
                    body,
                    status,
                    headersOf(
                        HttpHeaders.ContentType,
                        contentType.toString(),
                    ),
                )
            },
        ) {
            expectSuccess = true
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        return runCatching { client.get("https://bookk.test") }.exceptionOrNull()!!
    }

    @Test
    fun `maps forbidden access suspended response to business access suspended`() = runUnitTest {
        given()
        val failure = failureFor(
            HttpStatusCode.Forbidden,
            ServerErrorCodes.BUSINESS_EMPLOYEE_ACCESS_SUSPENDED,
            "suspended",
        )

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
        val failure = failureFor(
            HttpStatusCode.UnprocessableEntity,
            ServerErrorCodes.BUSINESS_EMPLOYEE_ACCESS_SUSPENDED,
        )

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.BusinessError>(result)
    }

    @Test
    fun `maps plain text 404 to not found preserving the cause`() = runUnitTest {
        given()
        val failure = failureFor(
            HttpStatusCode.NotFound,
            "Not found".encodeToByteArray(),
            ContentType.Text.Plain,
        )

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.NotFound>(result)
        assertSame(failure, result.cause)
    }

    @Test
    fun `maps an empty 404 response to not found`() = runUnitTest {
        given()
        val failure = failureFor(
            HttpStatusCode.NotFound,
            byteArrayOf(),
            ContentType.Application.ProtoBuf,
        )

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.NotFound>(result)
    }

    @Test
    fun `preserves structured business errors on 404`() = runUnitTest {
        given()
        val failure = failureFor(HttpStatusCode.NotFound, 200024, "Employee not found")

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.BusinessError>(result)
        assertEquals(200024, result.errorCode)
        assertEquals("Employee not found", result.message)
    }

    @Test
    fun `keeps unrelated HTTP failures as unknown API errors`() = runUnitTest {
        given()
        val failure = failureFor(
            HttpStatusCode.Conflict,
            "Conflict".encodeToByteArray(),
            ContentType.Text.Plain,
        )

        whenn()
        val result = failure.toDomain()

        then()
        assertIs<Error.UnknownApiError>(result)
        assertEquals(409, result.errorCode)
    }
}
