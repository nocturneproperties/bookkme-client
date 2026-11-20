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
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.TelegramSignInRequestRemote
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramSignInData
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TelegramSignInHttpTest {
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
        var path: String? = null
        var request: TelegramSignInRequestRemote? = null
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                request = dataSerializer.decodeFromByteArray(
                    TelegramSignInRequestRemote.serializer(),
                    (it.body as OutgoingContent.ByteArrayContent).bytes(),
                )
                respond(
                    dataSerializer.encodeToByteArray(
                        TokenInfoResponse.serializer(),
                        TokenInfoResponse("access", "refresh"),
                    ),
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }

        init {
            every { provider.get(any()) } returns preferences
        }

        val sut = CommonAuthorizationDataSource(client, client, provider)
    }

    private fun signInData(documents: AcceptedLegalDocuments?) = TelegramSignInData(
        initData = "auth_date=1&hash=00",
        deviceInfo = SignInData.DeviceInfo("device-uuid", "Telegram"),
        acceptedDocuments = documents,
    )

    @Test
    fun `posts the init data and device to the telegram sign in route`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.signInWithTelegram(signInData(null))

        then()
        assertEquals("/api/auth/telegram/sign_in", fixture.path)
        assertEquals("auth_date=1&hash=00", fixture.request?.initData)
        assertEquals("device-uuid", fixture.request?.deviceInfo?.deviceUUID)
        assertEquals("Telegram", fixture.request?.deviceInfo?.deviceName)
        assertEquals(TokenInfo("access", "refresh"), result)
    }

    @Test
    fun `omits accepted documents for a returning user`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.signInWithTelegram(signInData(null))

        then()
        assertNull(fixture.request?.acceptedDocuments)
    }

    @Test
    fun `sends the accepted document versions for a new user`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.signInWithTelegram(signInData(AcceptedLegalDocuments("2026-01", "2026-02")))

        then()
        assertEquals("2026-01", fixture.request?.acceptedDocuments?.privacyPolicy)
        assertEquals("2026-02", fixture.request?.acceptedDocuments?.termsOfService)
    }
}
