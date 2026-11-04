package me.bookk.feature.settings.data.datasource

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
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.data.remote.model.DataExportRemote
import me.bookk.feature.settings.data.remote.model.DataExportRequestRemote
import me.bookk.feature.settings.data.remote.model.DataExportStatusRemote
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CommonDataExportDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val id = Uuid.parse("00000000-0000-0000-0000-000000000001")

    @Test
    fun `starts an export with the confirmed token at the documented endpoint`() = runUnitTest {
        given()
        var path: String? = null
        var method: HttpMethod? = null
        var token: String? = null
        val bytes = dataSerializer.encodeToByteArray(
            DataExportRemote.serializer(),
            DataExportRemote(DataExportStatusRemote.RUNNING),
        )
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                method = it.method
                token =
                    dataSerializer.decodeFromByteArray(
                        DataExportRequestRemote.serializer(),
                        (it.body as OutgoingContent.ByteArrayContent).bytes(),
                    ).stepUpToken
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
        val dataSource = CommonDataExportDataSource(client)

        whenn()
        val result = try {
            dataSource.start("one-use")
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/user/export", path)
        assertEquals(HttpMethod.Post, method)
        assertEquals("one-use", token)
        assertEquals(DataExportStatus.RUNNING, result.status)
    }

    @Test
    fun `reads ready status with the backend expiry and cooldown`() = runUnitTest {
        given()
        val requested = Instant.parse("2026-11-04T00:00:00Z")
        val expires = Instant.parse("2026-11-07T00:00:00Z")
        val available = Instant.parse("2026-11-05T00:00:00Z")
        val bytes = dataSerializer.encodeToByteArray(
            DataExportRemote.serializer(),
            DataExportRemote(DataExportStatusRemote.READY, requested, expires, available),
        )
        var path: String? = null
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                respond(
                    bytes,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }

        whenn()
        val result = try {
            CommonDataExportDataSource(client).get()
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/user/export", path)
        assertEquals(DataExportStatus.READY, result.status)
        assertEquals(requested, result.requestedAt)
        assertEquals(expires, result.expiresAt)
        assertEquals(available, result.availableAgainAt)
    }

    @Test
    fun `downloads JSON unchanged and explicitly requests JSON`() = runUnitTest {
        given()
        val json = "{\"records\":[1],\"description\":\"Zażółć\"}"
        var path: String? = null
        var accept: String? = null
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                accept = it.headers[HttpHeaders.Accept]
                respond(
                    json,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        ) { install(Resources) }

        whenn()
        val result = try {
            CommonDataExportDataSource(client).download()
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/user/export/download", path)
        assertEquals("application/json", accept)
        assertEquals(json, result)
    }
}
