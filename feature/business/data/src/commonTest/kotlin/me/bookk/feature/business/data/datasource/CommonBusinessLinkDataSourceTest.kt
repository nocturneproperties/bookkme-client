package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.data.remote.model.BusinessLinkRemote
import me.bookk.feature.business.data.remote.model.BusinessLinkRequest
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CommonBusinessLinkDataSourceTest {
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
            MockEngine {
                requests += it
                respondLink(businessId)
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val sut = CommonBusinessLinkDataSource(client, FakeBusinessLinkDao())

        private fun MockRequestHandleScope.respondLink(businessId: Uuid): HttpResponseData {
            return respond(
                dataSerializer.encodeToByteArray(
                    BusinessLinkRemote.serializer(),
                    BusinessLinkRemote(businessId, "my-salon"),
                ),
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
            )
        }
    }

    @Test
    fun `reads the business link from the documented endpoint`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = try {
            fixture.sut.getLink(fixture.businessId)
        } finally {
            fixture.client.close()
        }

        then()
        val request = fixture.requests.single()
        assertEquals("/api/business/${fixture.businessId}/link", request.url.encodedPath)
        assertEquals(HttpMethod.Get, request.method)
        assertEquals(BusinessLink(fixture.businessId, "my-salon"), result)
    }

    @Test
    fun `puts the link name to the documented endpoint`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = try {
            fixture.sut.setLink(fixture.businessId, "my-salon")
        } finally {
            fixture.client.close()
        }

        then()
        val request = fixture.requests.single()
        assertEquals("/api/business/${fixture.businessId}/link", request.url.encodedPath)
        assertEquals(HttpMethod.Put, request.method)
        assertEquals("my-salon", request.decodeLinkRequest().name)
        assertEquals(BusinessLink(fixture.businessId, "my-salon"), result)
    }

    private fun HttpRequestData.decodeLinkRequest(): BusinessLinkRequest {
        return dataSerializer.decodeFromByteArray(
            BusinessLinkRequest.serializer(),
            (body as OutgoingContent.ByteArrayContent).bytes(),
        )
    }
}
