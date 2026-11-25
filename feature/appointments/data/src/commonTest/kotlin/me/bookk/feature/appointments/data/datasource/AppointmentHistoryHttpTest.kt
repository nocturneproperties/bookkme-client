package me.bookk.feature.appointments.data.datasource

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
import me.bookk.database.dao.AppointmentDao
import me.bookk.feature.appointments.data.remote.model.AppointmentPageRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class AppointmentHistoryHttpTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val businessId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000001")

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
        var page = AppointmentPageRemote(data = emptyList(), nextCursor = null)
        val client = HttpClient(
            MockEngine {
                request = it
                respond(
                    dataSerializer.encodeToByteArray(
                        AppointmentPageRemote.serializer(),
                        page,
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
        private val provider = mock<PreferenceProvider> {
            every { get(any()) } returns mock<Preferences>()
        }
        val sut =
            CommonAppointmentDataSource(client, mock<AppointmentDao>(), provider)

        suspend fun <T> call(action: suspend CommonAppointmentDataSource.() -> T): T {
            return try {
                sut.action()
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `asks the history for the page after the cursor with the search`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.call { getAppointmentHistory(businessId, search = "anna", cursor = "after-a") }

        then()
        assertEquals(
            "/api/appointments/history/$businessId?cursor=after-a&query=anna",
            fixture.request?.url?.encodedPathAndQuery,
        )
    }

    @Test
    fun `returns the next cursor of the page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.page = AppointmentPageRemote(data = emptyList(), nextCursor = "after-b")

        whenn()
        val page = fixture.call { getAppointmentHistory(businessId, search = null, cursor = null) }

        then()
        assertEquals(Page(emptyList<Appointment>(), nextCursor = "after-b"), page)
    }

    @Test
    fun `returns no cursor for the last page`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val page = fixture.call { getAppointmentHistory(businessId, search = null, cursor = null) }

        then()
        assertEquals(Page(emptyList<Appointment>(), nextCursor = null), page)
    }
}
