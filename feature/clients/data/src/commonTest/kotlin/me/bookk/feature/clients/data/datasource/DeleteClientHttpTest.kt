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
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.ClientsDao
import me.bookk.feature.clients.data.remote.model.DeleteClientRequest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteClientHttpTest {
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
        val dao = mock<ClientsDao>()
        init {
            every { provider.get(any()) } returns preferences
        }
    }

    @Test
    fun `sends the documented request body at the correct route`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        var path: String? = null
        var method: HttpMethod? = null
        var token: String? = null
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                method = it.method
                token =
                    dataSerializer.decodeFromByteArray(
                        DeleteClientRequest.serializer(),
                        (it.body as OutgoingContent.ByteArrayContent).bytes(),
                    ).confirmationToken
                respond("", HttpStatusCode.NoContent)
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val dataSource = CommonClientsDataSource(client, fixture.dao, fixture.provider)

        whenn()
        try {
            dataSource.deleteClient(id, id, "one-use")
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/business/$id/clients/$id", path)
        assertEquals(HttpMethod.Delete, method)
        assertEquals("one-use", token)
    }
}
