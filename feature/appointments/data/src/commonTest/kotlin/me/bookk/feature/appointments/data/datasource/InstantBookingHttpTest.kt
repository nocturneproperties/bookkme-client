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
import kotlinx.datetime.LocalDateTime
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.AppointmentDao
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.data.remote.model.InstantAppointmentDraftRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.InstantAppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class InstantBookingHttpTest {
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
        val dao = mock<AppointmentDao>()
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
        var request: InstantAppointmentDraftRemote? = null
        val response = Appointment.stub(date = LocalDateTime(2026, 11, 5, 10, 0)).toRemote()
        val bytes = dataSerializer.encodeToByteArray(AppointmentRemote.serializer(), response)
        val client = HttpClient(
            MockEngine {
                path = it.url.encodedPath
                request =
                    dataSerializer.decodeFromByteArray(
                        InstantAppointmentDraftRemote.serializer(),
                        (it.body as OutgoingContent.ByteArrayContent).bytes(),
                    )
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
        val dataSource = CommonAppointmentDataSource(client, fixture.dao, fixture.provider)
        val draft =
            InstantAppointmentDraft(
                id,
                id,
                listOf(RequestedService(id, 3)),
                Instant.parse("2026-11-05T10:00:00Z"),
                "note",
                "fresh-offer",
                id,
            )

        whenn()
        val result = try {
            dataSource.createAppointment(draft)
        } finally {
            client.close()
        }

        then()
        assertEquals("/api/appointments/instant", path)
        assertEquals(response.id, result.id)
        assertEquals(id, request?.businessId)
        assertEquals(id, request?.employeeId)
        assertEquals(id, request?.clientId)
        assertEquals(id, request?.services?.single()?.serviceId)
        assertEquals(3, request?.services?.single()?.count)
        assertEquals(draft.date, request?.date)
        assertEquals("note", request?.note)
        assertEquals("fresh-offer", request?.offerToken)
    }
}
