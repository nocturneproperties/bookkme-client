package me.bookk.feature.business.data.datasource

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
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlinx.serialization.builtins.ListSerializer
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.remote.model.BusinessPermissionsRemote
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.BusinessesByIdsRequest
import me.bookk.feature.business.data.remote.model.ResourcePermissionRemote
import me.bookk.feature.business.data.remote.model.ScheduleRemote
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class BusinessesByIdsHttpTest {

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
        val requests = mutableListOf<HttpRequestData>()
        val client = HttpClient(
            MockEngine { request ->
                requests += request
                val ids = request.decodeIds()
                respond(
                    dataSerializer.encodeToByteArray(
                        ListSerializer(BusinessRemote.serializer()),
                        ids.map { remoteBusiness(it) },
                    ),
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
        val sut = CommonBusinessDataSource(client, mock<BusinessDao>())

        suspend fun fetch(ids: Set<Uuid>) = try {
            sut.getBusinessesByIdsFromRemote(ids)
        } finally {
            client.close()
        }
    }

    @Test
    fun `posts the ids to the bulk route and maps the businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ids = setOf(Uuid.random(), Uuid.random())

        whenn()
        val result = fixture.fetch(ids)

        then()
        val request = fixture.requests.single()
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/business/bulk", request.url.encodedPath)
        assertEquals(ids.toList(), request.decodeIds())
        assertEquals(ids.toList(), result.map { it.id })
        assertEquals(listOf("Studio", "Studio"), result.map { it.name })
    }

    @Test
    fun `splits more than one hundred ids into several requests`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ids = List(150) { Uuid.random() }.toSet()

        whenn()
        val result = fixture.fetch(ids)

        then()
        assertEquals(listOf(100, 50), fixture.requests.map { it.decodeIds().size })
        assertEquals(ids.toList(), result.map { it.id })
    }

    @Test
    fun `makes no request without ids`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.fetch(emptySet())

        then()
        assertEquals(emptyList(), fixture.requests)
        assertEquals(emptyList(), result)
    }

    private companion object {
        fun HttpRequestData.decodeIds(): List<Uuid> {
            val bytes = (body as OutgoingContent.ByteArrayContent).bytes()
            return dataSerializer.decodeFromByteArray(
                BusinessesByIdsRequest.serializer(),
                bytes,
            ).ids
        }

        fun remoteBusiness(id: Uuid): BusinessRemote {
            val permission = ResourcePermissionRemote()
            return BusinessRemote(
                id = id,
                name = "Studio",
                description = "",
                address = "",
                timeZone = TimeZone.UTC,
                location = null,
                currencyCode = "UAH",
                socials = emptyList(),
                schedule = ScheduleRemote(emptyMap(), emptyList()),
                permissions = BusinessPermissionsRemote(
                    permission,
                    permission,
                    permission,
                    permission,
                    permission,
                ),
                ownerId = Uuid.NIL,
                employeeId = null,
                clientId = Uuid.random(),
            )
        }
    }
}
