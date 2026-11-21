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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.builtins.ListSerializer
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.entity.LegalDocumentStatusEntity
import me.bookk.feature.settings.data.remote.model.AcceptedLegalDocumentsRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentStatusRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentVersionRemote
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

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

    private class FakeLegalDocumentStatusDao : LegalDocumentStatusDao() {
        val statuses = MutableStateFlow<List<LegalDocumentStatusEntity>>(emptyList())

        override fun observeAll(): Flow<List<LegalDocumentStatusEntity>> {
            return statuses
        }

        override suspend fun upsertAll(statuses: List<LegalDocumentStatusEntity>) {
            this.statuses.value =
                (this.statuses.value + statuses).associateBy { it.document }.values.toList()
        }

        override suspend fun clear() {
            statuses.value = emptyList()
        }

        override suspend fun replaceAll(statuses: List<LegalDocumentStatusEntity>) {
            this.statuses.value = statuses
        }
    }

    private class Fixture(
        val client: HttpClient =
            HttpClient(MockEngine { error("Local operations must not call the network") }),
    ) {
        val dao = FakeLegalDocumentStatusDao()
        val sut = CommonLegalDocumentsDataSource(client, dao)
        val statuses = listOf(
            LegalDocumentStatus(
                LegalDocumentVersion(
                    LegalDocument.PRIVACY_POLICY,
                    "privacy-v2",
                    "https://example.com/privacy?a=1&b=2",
                    Instant.parse("2026-11-01T00:00:00.123Z"),
                ),
                null,
            ),
            LegalDocumentStatus(
                LegalDocumentVersion(
                    LegalDocument.TERMS_OF_SERVICE,
                    "terms-v3",
                    "https://example.com/terms",
                    Instant.parse("2026-10-01T00:00:00Z"),
                ),
                Instant.parse("2026-11-02T00:00:00.987Z"),
            ),
        )
    }

    @Test
    fun `emits an empty list when the database has no statuses`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.observeStatuses().first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `restores the complete status snapshot after recreation`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveStatuses(fixture.statuses)
        val recreated = CommonLegalDocumentsDataSource(fixture.client, fixture.dao)

        whenn()
        val result = recreated.observeStatuses().first()

        then()
        assertEquals(fixture.statuses, result)
    }

    @Test
    fun `persists an empty status list`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveStatuses(fixture.statuses)

        whenn()
        fixture.sut.saveStatuses(emptyList())

        then()
        val recreated = CommonLegalDocumentsDataSource(fixture.client, fixture.dao)
        assertEquals(emptyList(), recreated.observeStatuses().first())
    }

    @Test
    fun `notifies active observers when the snapshot is replaced`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveStatuses(fixture.statuses)
        val emissions = mutableListOf<List<LegalDocumentStatus>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observeStatuses().collect { emissions.add(it) }
        }
        val updated = listOf(
            fixture.statuses.first().copy(acceptedAt = Instant.parse("2026-11-03T00:00:00Z")),
        )

        whenn()
        fixture.sut.saveStatuses(updated)

        then()
        assertEquals(listOf<List<LegalDocumentStatus>>(fixture.statuses, updated), emissions)
    }

    @Test
    fun `clears the persisted status and notifies observers on logout`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveStatuses(fixture.statuses)
        val emissions = mutableListOf<List<LegalDocumentStatus>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observeStatuses().collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.doOnLogOut()

        then()
        val recreated = CommonLegalDocumentsDataSource(fixture.client, fixture.dao)
        assertEquals(emptyList(), recreated.observeStatuses().first())
        assertTrue(fixture.dao.statuses.value.isEmpty())
        assertEquals(listOf<List<LegalDocumentStatus>>(fixture.statuses, emptyList()), emissions)
    }

    @Test
    fun `stores status models directly in database rows`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.saveStatuses(fixture.statuses)

        then()
        assertEquals(
            listOf("PRIVACY_POLICY", "TERMS_OF_SERVICE"),
            fixture.dao.statuses.value.map {
                it.document
            },
        )
        assertEquals(
            listOf("privacy-v2", "terms-v3"),
            fixture.dao.statuses.value.map {
                it.version
            },
        )
    }

    @Test
    fun `exposes saved database rows without a sync marker`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.saveStatuses(fixture.statuses)

        then()
        assertEquals(2, fixture.dao.statuses.value.size)
        assertEquals(fixture.statuses, fixture.sut.observeStatuses().first())
    }

    @Test
    fun `getVersions decodes protobuf from the documented endpoint`() = runUnitTest {
        given()
        val remote =
            LegalDocumentVersionRemote(
                LegalDocumentRemote.PRIVACY_POLICY,
                "2026-11",
                "https://example.com/privacy",
                Instant.parse("2026-11-01T00:00:00Z"),
            )
        val bytes = dataSerializer.encodeToByteArray(
            ListSerializer(LegalDocumentVersionRemote.serializer()),
            listOf(remote),
        )
        var requestPath: String? = null
        val client = HttpClient(
            MockEngine {
                requestPath = it.url.encodedPath
                respond(
                    bytes,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val fixture = Fixture(client)

        whenn()
        val result = try {
            fixture.sut.getVersions()
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
        val remote =
            LegalDocumentStatusRemote(
                LegalDocumentVersionRemote(
                    LegalDocumentRemote.TERMS_OF_SERVICE,
                    "2026-11",
                    "https://example.com/terms",
                    Instant.parse("2026-11-01T00:00:00Z"),
                ),
                null,
            )
        val bytes = dataSerializer.encodeToByteArray(
            ListSerializer(LegalDocumentStatusRemote.serializer()),
            listOf(remote),
        )
        var requestPath: String? = null
        val client = HttpClient(
            MockEngine {
                requestPath = it.url.encodedPath
                respond(
                    bytes,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val fixture = Fixture(client)

        whenn()
        val result = try {
            fixture.sut.getStatuses()
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
        val client = HttpClient(
            MockEngine {
                requestPath = it.url.encodedPath
                method = it.method
                sent =
                    dataSerializer.decodeFromByteArray(
                        AcceptedLegalDocumentsRemote.serializer(),
                        (it.body as OutgoingContent.ByteArrayContent).bytes(),
                    )
                respond("", HttpStatusCode.NoContent)
            },
        ) {
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val fixture = Fixture(client)

        whenn()
        try {
            fixture.sut.accept(AcceptedLegalDocuments(privacyPolicy = "privacy-v3"))
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
