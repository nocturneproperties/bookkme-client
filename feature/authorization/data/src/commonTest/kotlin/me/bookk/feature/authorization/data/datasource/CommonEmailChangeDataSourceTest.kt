package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import io.ktor.utils.io.ByteReadChannel
import me.bookk.core.data.BusinessServerError
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.EmailChangeRequestRemote
import me.bookk.feature.authorization.data.remote.model.EmailConfirmationRemote
import me.bookk.feature.authorization.data.remote.model.EmailVerificationStartedRemote
import me.bookk.feature.authorization.domain.entity.EmailVerification
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

class CommonEmailChangeDataSourceTest {

    private class Fixture {
        val verificationId = Uuid.parse("00000000-0000-0000-0000-0000000000b2")
        var request: HttpRequestData? = null
        var requestBody: ByteArray = byteArrayOf()
        var status = HttpStatusCode.OK
        var body: ByteArray = byteArrayOf()
        val client = HttpClient(
            MockEngine {
                request = it
                requestBody = it.body.toByteArray()
                respond(
                    ByteReadChannel(body),
                    status,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            expectSuccess = true
            install(Resources)
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val sut = CommonEmailChangeDataSource(client)

        fun respondWithVerification() {
            body = dataSerializer.encodeToByteArray(
                EmailVerificationStartedRemote.serializer(),
                EmailVerificationStartedRemote(
                    verificationId = verificationId,
                    expiresAt = Instant.fromEpochSeconds(1_000),
                    resendAvailableAt = Instant.fromEpochSeconds(100),
                ),
            )
        }

        fun respondWithBusinessError(code: Int) {
            status = HttpStatusCode.UnprocessableEntity
            body = dataSerializer.encodeToByteArray(
                BusinessServerError.serializer(),
                BusinessServerError(code, "rejected"),
            )
        }
    }

    @Test
    fun `posts the new address to start an email change`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.respondWithVerification()

        whenn()
        fixture.sut.startEmailChange("new@example.com")

        then()
        assertEquals(HttpMethod.Post, fixture.request!!.method)
        assertEquals("/api/auth/email", fixture.request!!.url.encodedPath)
        val sent = dataSerializer.decodeFromByteArray(
            EmailChangeRequestRemote.serializer(),
            fixture.requestBody,
        )
        assertEquals("new@example.com", sent.email)
    }

    @Test
    fun `returns the started email verification`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.respondWithVerification()

        whenn()
        val result = fixture.sut.startEmailChange("new@example.com")

        then()
        assertEquals(
            EmailVerification(
                id = fixture.verificationId,
                expiresAt = Instant.fromEpochSeconds(1_000),
                resendAvailableAt = Instant.fromEpochSeconds(100),
            ),
            result,
        )
    }

    @Test
    fun `surfaces the business error when the email change is refused`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.respondWithBusinessError(910005)

        whenn()
        val result = runCatching { fixture.sut.startEmailChange("new@example.com") }

        then()
        assertEquals(910005, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }

    @Test
    fun `posts the verification id and code to confirm an email change`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.status = HttpStatusCode.NoContent

        whenn()
        fixture.sut.confirmEmailChange(fixture.verificationId, "123456")

        then()
        assertEquals(HttpMethod.Post, fixture.request!!.method)
        assertEquals("/api/auth/email/confirm", fixture.request!!.url.encodedPath)
        val sent = dataSerializer.decodeFromByteArray(
            EmailConfirmationRemote.serializer(),
            fixture.requestBody,
        )
        assertEquals(fixture.verificationId, sent.verificationId)
        assertEquals("123456", sent.code)
    }

    @Test
    fun `surfaces the business error when the code is refused`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.respondWithBusinessError(910001)

        whenn()
        val result = runCatching { fixture.sut.confirmEmailChange(fixture.verificationId, "000000") }

        then()
        assertEquals(910001, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }
}
