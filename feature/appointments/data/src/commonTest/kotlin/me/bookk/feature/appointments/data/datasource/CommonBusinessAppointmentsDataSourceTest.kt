package me.bookk.feature.appointments.data.datasource

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
import me.bookk.feature.appointments.data.remote.model.BusinessAppointmentsEnabledRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CommonBusinessAppointmentsDataSourceTest {
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
    fun `getEnabled decodes protobuf from the documented endpoint`() = runUnitTest {
        given()
        val remote = BusinessAppointmentsEnabledRemote(Uuid.parse("00000000-0000-0000-0000-000000000001"), false)
        val bytes = dataSerializer.encodeToByteArray(ListSerializer(BusinessAppointmentsEnabledRemote.serializer()), listOf(remote))
        var requestPath: String? = null
        val client = HttpClient(MockEngine {
            requestPath = it.url.encodedPath
            respond(bytes, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()))
        }) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val dataSource = CommonBusinessAppointmentsDataSource(client)

        whenn()
        val result = try {
            dataSource.getEnabled()
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/appointments/enabled", requestPath)
        assertEquals(listOf(remote.toDomain()), result)
    }
}
