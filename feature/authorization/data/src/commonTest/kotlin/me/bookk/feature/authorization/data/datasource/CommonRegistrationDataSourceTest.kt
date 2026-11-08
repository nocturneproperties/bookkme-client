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
import me.bookk.feature.authorization.data.remote.model.EmailVerificationStartedRemote
import me.bookk.feature.authorization.data.remote.model.SignUpChallengeResponseRemote
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.data.remote.model.VerifyAccountCreationRequest
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

class CommonRegistrationDataSourceTest {

    private class Fixture {
        val verificationId = Uuid.parse("00000000-0000-0000-0000-0000000000a1")
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
        val sut = CommonRegistrationDataSource(client)

        fun respondWithChallenge() {
            body = dataSerializer.encodeToByteArray(
                SignUpChallengeResponseRemote.serializer(),
                SignUpChallengeResponseRemote(
                    requestId = "req",
                    challenge = "chal",
                    challengeJson = "{}",
                    userHandle = "handle",
                    displayName = "John Doe",
                    emailVerification = EmailVerificationStartedRemote(
                        verificationId = verificationId,
                        expiresAt = Instant.fromEpochSeconds(1_000),
                        resendAvailableAt = Instant.fromEpochSeconds(100),
                    ),
                ),
            )
        }

        fun registrationData(): RegistrationData {
            return RegistrationData(
                requestId = "req",
                deviceInfo = RegistrationData.DeviceInfo(
                    "00000000-0000-0000-0000-000000000001",
                    "Device",
                ),
                userInfo = RegistrationData.UserInfo("John", "Doe", "john@example.com"),
                publicKeyCredentialJson = "{}",
                acceptedDocuments = AcceptedLegalDocuments("privacy-v3", "terms-v2"),
                verificationId = verificationId,
                emailCode = "123456",
            )
        }
    }

    @Test
    fun `reads the email verification from the sign up challenge`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.respondWithChallenge()

        whenn()
        val result = fixture.sut.getSignUpPasskeyChallenge(
            StartSignUp.UserData("John", "Doe", "john@example.com"),
        )

        then()
        assertEquals(HttpMethod.Post, fixture.request!!.method)
        assertEquals("/api/auth/passkey/sign_up/challenge", fixture.request!!.url.encodedPath)
        assertEquals("req", result.requestId)
        assertEquals(fixture.verificationId, result.emailVerification.id)
        assertEquals(Instant.fromEpochSeconds(1_000), result.emailVerification.expiresAt)
        assertEquals(Instant.fromEpochSeconds(100), result.emailVerification.resendAvailableAt)
    }

    @Test
    fun `posts the verification id and code on sign up`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.body = dataSerializer.encodeToByteArray(
            TokenInfoResponse.serializer(),
            TokenInfoResponse("access", "refresh"),
        )

        whenn()
        val result = fixture.sut.finishRegistration(fixture.registrationData())

        then()
        assertEquals(HttpMethod.Post, fixture.request!!.method)
        assertEquals("/api/auth/sign_up", fixture.request!!.url.encodedPath)
        val sent = dataSerializer.decodeFromByteArray(
            VerifyAccountCreationRequest.serializer(),
            fixture.requestBody,
        )
        assertEquals(fixture.verificationId, sent.verificationId)
        assertEquals("123456", sent.emailCode)
        assertEquals("privacy-v3", sent.acceptedDocuments.privacyPolicy)
        assertEquals("access", result.accessToken)
    }

    @Test
    fun `surfaces the email code business error on sign up`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.status = HttpStatusCode.UnprocessableEntity
        fixture.body = dataSerializer.encodeToByteArray(
            BusinessServerError.serializer(),
            BusinessServerError(910001, "wrong"),
        )

        whenn()
        val result = runCatching { fixture.sut.finishRegistration(fixture.registrationData()) }

        then()
        assertEquals(910001, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }
}
