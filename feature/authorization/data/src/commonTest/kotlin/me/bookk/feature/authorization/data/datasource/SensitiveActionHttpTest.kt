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
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid
import me.bookk.feature.authorization.data.remote.model.StepUpRequestRemote
import me.bookk.feature.authorization.data.remote.model.StepUpTokenRemote
import me.bookk.feature.authorization.data.remote.model.SensitiveActionRemote
import me.bookk.feature.authorization.domain.datasource.authorization.SensitiveActionConfirmation
import me.bookk.feature.authorization.domain.entity.SensitiveAction

@OptIn(ExperimentalCoroutinesApi::class)
class SensitiveActionHttpTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    @BeforeTest
    fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    private class Fixture {
        val provider = mock<PreferenceProvider>()
        val preferences = mock<Preferences>()

        init { every { provider.get(any()) } returns preferences }
    }

    @Test
    fun `sends the documented request body at the correct route`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        var request: StepUpRequestRemote? = null
        var path: String? = null
        val bytes = dataSerializer.encodeToByteArray(StepUpTokenRemote.serializer(), StepUpTokenRemote("one-use"))
        val client = HttpClient(MockEngine {
            path = it.url.encodedPath
            request = dataSerializer.decodeFromByteArray(StepUpRequestRemote.serializer(), (it.body as OutgoingContent.ByteArrayContent).bytes())
            respond(bytes, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()))
        }) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val dataSource = CommonAuthorizationDataSource(client, client, fixture.provider)

        whenn()
        val result = try {
            dataSource.confirmSensitiveAction(SensitiveActionConfirmation("request", "assertion", SensitiveAction.EXPORT_BUSINESS_DATA, id, id))
        } finally { client.close() }

        then()
        assertEquals("/api/auth/passkey/step_up", path)
        assertEquals("one-use", result)
        assertEquals("request", request?.requestId)
        assertEquals("assertion", request?.publicKeyCredentialJson)
        assertEquals(SensitiveActionRemote.EXPORT_BUSINESS_DATA, request?.action)
        assertEquals(id, request?.businessId)
        assertEquals(id, request?.targetId)
    }
}
