package me.bookk.feature.settings.data.datasource

import io.ktor.client.plugins.defaultRequest
import io.ktor.http.contentType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.data.remote.model.LegalDocumentRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentStatusRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentVersionRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.test.assertNull
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import me.bookk.feature.settings.data.remote.model.AcceptedLegalDocumentsRemote
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments

@OptIn(ExperimentalCoroutinesApi::class)
class CommonLegalDocumentsDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getVersions decodes protobuf from the documented endpoint`() = runUnitTest {
        given()
        val remote = LegalDocumentVersionRemote(LegalDocumentRemote.PRIVACY_POLICY, "2026-11", "https://example.com/privacy", Instant.parse("2026-11-01T00:00:00Z"))
        val bytes = dataSerializer.encodeToByteArray(ListSerializer(LegalDocumentVersionRemote.serializer()), listOf(remote))
        var requestPath: String? = null
        val client = HttpClient(MockEngine {
            requestPath = it.url.encodedPath
            respond(bytes, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()))
        }) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val dataSource = CommonLegalDocumentsDataSource(client)

        whenn()
        val result = try {
            dataSource.getVersions()
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/user/legal", requestPath)
        assertEquals(listOf(remote.toDomain()), result)
    }

    @Test
    fun `getStatuses decodes protobuf from the documented endpoint`() = runUnitTest {
        given()
        val remote = LegalDocumentStatusRemote(LegalDocumentVersionRemote(LegalDocumentRemote.TERMS_OF_SERVICE, "2026-11", "https://example.com/terms", Instant.parse("2026-11-01T00:00:00Z")), null)
        val bytes = dataSerializer.encodeToByteArray(ListSerializer(LegalDocumentStatusRemote.serializer()), listOf(remote))
        var requestPath: String? = null
        val client = HttpClient(MockEngine {
            requestPath = it.url.encodedPath
            respond(bytes, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()))
        }) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val dataSource = CommonLegalDocumentsDataSource(client)

        whenn()
        val result = try {
            dataSource.getStatuses()
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/user/me/legal", requestPath)
        assertEquals(listOf(remote.toDomain()), result)
    }
    @Test
    fun `acceptance posts exactly the chosen document versions`() = runUnitTest {
        given()
        var requestPath: String? = null
        var method: HttpMethod? = null
        var sent: AcceptedLegalDocumentsRemote? = null
        val client = HttpClient(MockEngine {
            requestPath = it.url.encodedPath
            method = it.method
            sent = dataSerializer.decodeFromByteArray(AcceptedLegalDocumentsRemote.serializer(), (it.body as OutgoingContent.ByteArrayContent).bytes())
            respond("", HttpStatusCode.NoContent)
        }) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val dataSource = CommonLegalDocumentsDataSource(client)

        whenn()
        try {
            dataSource.accept(AcceptedLegalDocuments(privacyPolicy = "privacy-v3"))
        } finally {
            client.close()
        }

        then()
        assertEquals(HttpMethod.Post, method)
        assertEquals("/api/user/me/legal", requestPath)
        assertEquals("privacy-v3", sent?.privacyPolicy)
        assertNull(sent?.termsOfService)
    }

}
