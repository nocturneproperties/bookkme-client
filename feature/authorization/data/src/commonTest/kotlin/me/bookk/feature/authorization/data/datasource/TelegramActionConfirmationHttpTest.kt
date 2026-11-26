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
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.ActionConfirmationTokenRemote
import me.bookk.feature.authorization.data.remote.model.SensitiveActionRemote
import me.bookk.feature.authorization.data.remote.model.TelegramActionConfirmationRequestRemote
import me.bookk.feature.authorization.data.remote.model.TelegramActionConfirmationStartRemote
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramActionConfirmationStart
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class TelegramActionConfirmationHttpTest {
    private class Fixture(
        private val answer: () -> ByteArray = { ByteArray(0) },
        private val status: HttpStatusCode = HttpStatusCode.NoContent,
    ) {
        val provider = mock<PreferenceProvider> {
            every { get(any()) } returns mock<Preferences>()
        }
        var method: HttpMethod? = null
        var path: String? = null
        var body: ByteArray? = null
        val client = HttpClient(
            MockEngine {
                method = it.method
                path = it.url.encodedPath
                body = (it.body as OutgoingContent.ByteArrayContent).bytes()
                respond(
                    answer(),
                    status,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val sut = CommonAuthorizationDataSource(client, client, provider, mock())
    }

    @Test
    fun `starts the telegram confirmation of the action at the start route`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")

        whenn()
        fixture.sut.startTelegramActionConfirmation(
            TelegramActionConfirmationStart(SensitiveAction.DELETE_ACCOUNT, Uuid.NIL, userId),
        )

        then()
        val sent = dataSerializer.decodeFromByteArray(
            TelegramActionConfirmationStartRemote.serializer(),
            requireNotNull(fixture.body),
        )
        assertEquals(HttpMethod.Post, fixture.method)
        assertEquals("/api/auth/telegram/confirm_action/start", fixture.path)
        assertEquals(SensitiveActionRemote.DELETE_ACCOUNT, sent.action)
        assertEquals(Uuid.NIL, sent.businessId)
        assertEquals(userId, sent.targetId)
    }

    @Test
    fun `sends the raw init data to the confirm route`() = runUnitTest {
        given()
        val fixture = Fixture(answer = ::token, status = HttpStatusCode.OK)

        whenn()
        fixture.sut.confirmActionWithTelegram(INIT_DATA)

        then()
        val sent = dataSerializer.decodeFromByteArray(
            TelegramActionConfirmationRequestRemote.serializer(),
            requireNotNull(fixture.body),
        )
        assertEquals(HttpMethod.Post, fixture.method)
        assertEquals("/api/auth/telegram/confirm_action", fixture.path)
        assertEquals(INIT_DATA, sent.initData)
    }

    @Test
    fun `returns the action confirmation token telegram earned`() = runUnitTest {
        given()
        val fixture = Fixture(answer = ::token, status = HttpStatusCode.OK)

        whenn()
        val result = fixture.sut.confirmActionWithTelegram(INIT_DATA)

        then()
        assertEquals("single-use", result)
    }

    private companion object {
        const val INIT_DATA = "auth_date=1&start_param=op_delete-account_00&hash=00"

        fun token(): ByteArray {
            return dataSerializer.encodeToByteArray(
                ActionConfirmationTokenRemote.serializer(),
                ActionConfirmationTokenRemote("single-use"),
            )
        }
    }
}
