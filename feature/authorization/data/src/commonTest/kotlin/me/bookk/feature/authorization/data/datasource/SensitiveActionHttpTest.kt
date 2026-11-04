package me.bookk.feature.authorization.data.datasource

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.ActionConfirmationRequestRemote
import me.bookk.feature.authorization.data.remote.model.ActionConfirmationTokenRemote
import me.bookk.feature.authorization.data.remote.model.SensitiveActionRemote
import me.bookk.feature.authorization.domain.datasource.authorization.ActionConfirmationRequest
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class SensitiveActionHttpTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val provider = mock<PreferenceProvider>()
        val preferences = mock<Preferences>()

        init {
            every { provider.get(any()) } returns preferences
        }
    }

    @Test
    fun `sends the documented request body at the correct route`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        var request: ActionConfirmationRequestRemote? = null
        var path: String? = null
        val bytes = dataSerializer.encodeToByteArray(
            ActionConfirmationTokenRemote.serializer(),
            ActionConfirmationTokenRemote("one-use"),
        )
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                request =
                    dataSerializer.decodeFromByteArray(
                        ActionConfirmationRequestRemote.serializer(),
                        (it.body as OutgoingContent.ByteArrayContent).bytes(),
                    )
                respond(
                    bytes,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val dataSource = CommonAuthorizationDataSource(client, client, fixture.provider)

        whenn()
        val result = try {
            dataSource.confirmAction(
                ActionConfirmationRequest(
                    "request",
                    "assertion",
                    SensitiveAction.DELETE_ACCOUNT,
                    id,
                    id,
                ),
            )
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/auth/passkey/confirm_action", path)
        assertEquals("one-use", result)
        assertEquals("request", request?.requestId)
        assertEquals("assertion", request?.publicKeyCredentialJson)
        assertEquals(SensitiveActionRemote.DELETE_ACCOUNT, request?.action)
        assertEquals(id, request?.businessId)
        assertEquals(id, request?.targetId)
    }

    @Test
    fun `deletes the account with only the confirmation token in the protobuf body`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            var body: ByteArray? = null
            var path: String? = null
            var method: HttpMethod? = null
            val client = HttpClient(
                MockEngine {
                    path = it.url.encodedPath
                    method = it.method
                    body = (it.body as OutgoingContent.ByteArrayContent).bytes()
                    respond("", HttpStatusCode.NoContent)
                },
            ) {
                install(Resources)
                install(ContentNegotiation) { protobuf(dataSerializer) }
                defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            }
            val dataSource = CommonAuthorizationDataSource(client, client, fixture.provider)
            val expectedBody = dataSerializer.encodeToByteArray(
                AccountDeletionBody.serializer(),
                AccountDeletionBody("delete-account-confirmed"),
            )

            whenn()
            try {
                dataSource.deleteAccount(DeleteAccountRequest("delete-account-confirmed"))
            } finally {
                client.close()
            }

            then()
            assertEquals(HttpMethod.Delete, method)
            assertEquals("/api/auth/account", path)
            assertContentEquals(expectedBody, body)
        }

    @Serializable
    private class AccountDeletionBody(@ProtoNumber(1) val confirmationToken: String)
}
