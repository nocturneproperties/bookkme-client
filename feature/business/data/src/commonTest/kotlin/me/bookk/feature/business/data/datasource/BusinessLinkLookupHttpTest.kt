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
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.remote.model.BusinessLinkRemote
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class BusinessLinkLookupHttpTest {

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
        val businessId: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val requests = mutableListOf<HttpRequestData>()
        val client = HttpClient(
            MockEngine { request ->
                requests += request
                respond(
                    dataSerializer.encodeToByteArray(
                        BusinessLinkRemote.serializer(),
                        BusinessLinkRemote(businessId, "new-salon"),
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

        suspend fun resolve(name: String) = try {
            sut.resolveLink(name)
        } finally {
            client.close()
        }
    }

    @Test
    fun `gets the link name from the documented lookup endpoint`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.resolve("old-salon")

        then()
        val request = fixture.requests.single()
        assertEquals("/api/business/link/old-salon", request.url.encodedPath)
        assertEquals(HttpMethod.Get, request.method)
    }

    @Test
    fun `returns the business behind the link with its current name`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.resolve("old-salon")

        then()
        assertEquals(BusinessLink(fixture.businessId, "new-salon"), result)
    }
}
