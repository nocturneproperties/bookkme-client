package me.bookk.feature.clients.data.datasource

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
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.pagination.Page
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.ClientsDao
import me.bookk.feature.clients.data.remote.model.ClientPageRemote
import me.bookk.feature.clients.data.remote.model.ClientRemote
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientPageHttpTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val businessId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000001")
    private val clientId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000002")

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        var request: HttpRequestData? = null
        var page = ClientPageRemote(data = emptyList(), nextCursor = null)
        val client = HttpClient(
            MockEngine {
                request = it
                respond(
                    dataSerializer.encodeToByteArray(ClientPageRemote.serializer(), page),
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            expectSuccess = true
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        private val provider = mock<PreferenceProvider> {
            every { get(any()) } returns mock<Preferences>()
        }
        val sut = CommonClientsDataSource(client, mock<ClientsDao>(), provider)

        suspend fun <T> call(action: suspend CommonClientsDataSource.() -> T): T {
            return try {
                sut.action()
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `asks for the first page of the business's clients`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.call { getClients(businessId, search = null, cursor = null) }

        then()
        assertEquals("/api/business/$businessId/clients", fixture.request?.url?.encodedPathAndQuery)
    }

    @Test
    fun `sends the search and the cursor of the next page`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.call { getClients(businessId, search = "anna", cursor = "after-a") }

        then()
        assertEquals(
            "/api/business/$businessId/clients?cursor=after-a&query=anna",
            fixture.request?.url?.encodedPathAndQuery,
        )
    }

    @Test
    fun `returns the clients of the page with their creation time and the next cursor`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val createdAt = Instant.fromEpochMilliseconds(1_700_000_000_000)
            fixture.page = ClientPageRemote(
                data = listOf(
                    ClientRemote(
                        id = clientId,
                        name = "Anna",
                        lastName = "Kowalska",
                        phone = null,
                        email = null,
                        userId = null,
                        description = null,
                        createdAt = createdAt,
                    ),
                ),
                nextCursor = "after-anna",
            )

            whenn()
            val page = fixture.call { getClients(businessId, search = null, cursor = null) }

            then()
            val expected = Client.Detached(
                id = clientId,
                name = "Anna",
                lastName = "Kowalska",
                phone = null,
                email = null,
                businessId = businessId,
                createdAt = createdAt,
            )
            assertEquals(Page(listOf<Client>(expected), nextCursor = "after-anna"), page)
        }
}
