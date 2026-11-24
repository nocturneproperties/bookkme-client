package me.bookk.feature.appointments.data.datasource

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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsDayRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsRequestRemote
import me.bookk.feature.appointments.data.remote.model.RequestedServiceRemote
import me.bookk.feature.appointments.data.remote.model.SavedBusinessRemote
import me.bookk.feature.appointments.data.remote.model.UpcomingBookingsRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.FreeSlot
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientBookingHttpTest {

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
        var request: HttpRequestData? = null

        fun freeSlotsRequest(): FreeSlotsRequestRemote? {
            val content = request?.body as? OutgoingContent.ByteArrayContent ?: return null
            return dataSerializer.decodeFromByteArray(
                FreeSlotsRequestRemote.serializer(),
                content.bytes(),
            )
        }
        var status = HttpStatusCode.OK
        var body = ByteArray(0)
        val client = HttpClient(
            MockEngine {
                request = it
                respond(
                    body,
                    status,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            expectSuccess = true
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val sut = CommonClientBookingDataSource(
            client,
            mock<AppointmentDao>(),
            mock<AppointmentRequestDao>(),
            mock<SavedBusinessDao>(),
        )

        suspend fun <T> call(action: suspend CommonClientBookingDataSource.() -> T): T {
            return try {
                sut.action()
            } finally {
                client.close()
            }
        }
    }

    @Test
    fun `fetches upcoming appointments and requests`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub(date = LocalDateTime(2026, 11, 24, 14, 30))
        fixture.body = dataSerializer.encodeToByteArray(
            UpcomingBookingsRemote.serializer(),
            UpcomingBookingsRemote(appointments = listOf(appointment.toRemote())),
        )

        whenn()
        val result = fixture.call { getUpcomingBookings() }

        then()
        assertEquals(HttpMethod.Get, fixture.request?.method)
        assertEquals("/api/appointments/upcoming", fixture.request?.url?.encodedPath)
        assertEquals(UpcomingBookings(listOf(appointment), emptyList()), result)
    }

    @Test
    fun `reads an empty upcoming response as no bookings`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.call { getUpcomingBookings() }

        then()
        assertEquals(UpcomingBookings(emptyList(), emptyList()), result)
    }

    @Test
    fun `fetches saved businesses with their last visit`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val savedAt = Instant.fromEpochMilliseconds(1_700_000_000_000)
        val lastVisitAt = Instant.fromEpochMilliseconds(1_690_000_000_000)
        fixture.body = dataSerializer.encodeToByteArray(
            ListSerializer(SavedBusinessRemote.serializer()),
            listOf(
                SavedBusinessRemote(
                    businessId,
                    enabled = true,
                    savedAt = savedAt,
                    lastVisitAt = lastVisitAt,
                ),
            ),
        )

        whenn()
        val result = fixture.call { getSavedBusinesses() }

        then()
        assertEquals("/api/appointments/businesses", fixture.request?.url?.encodedPath)
        assertEquals(listOf(SavedBusinessEntry(businessId, true, savedAt, lastVisitAt)), result)
    }

    @Test
    fun `reads a saved business without the enabled flag as not enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val savedAt = Instant.fromEpochMilliseconds(1_700_000_000_000)
        fixture.body = dataSerializer.encodeToByteArray(
            ListSerializer(SavedBusinessRemote.serializer()),
            listOf(SavedBusinessRemote(businessId, enabled = false, savedAt = savedAt)),
        )

        whenn()
        val result = fixture.call { getSavedBusinesses() }

        then()
        assertEquals(listOf(SavedBusinessEntry(businessId, false, savedAt, null)), result)
    }

    @Test
    fun `saves a business by POST`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.status = HttpStatusCode.NoContent

        whenn()
        fixture.call { saveBusiness(businessId) }

        then()
        assertEquals(HttpMethod.Post, fixture.request?.method)
        assertEquals("/api/appointments/businesses/$businessId", fixture.request?.url?.encodedPath)
    }

    @Test
    fun `asks for free slots by POST with the employee, services and dates`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employeeId = Uuid.random()
        val serviceId = Uuid.random()
        val query = FreeSlotsQuery(
            businessId = businessId,
            employeeId = employeeId,
            services = listOf(RequestedService(serviceId, 2)),
            from = LocalDate(2026, 11, 23),
            to = LocalDate(2026, 11, 29),
        )
        fixture.body = dataSerializer.encodeToByteArray(
            ListSerializer(FreeSlotsDayRemote.serializer()),
            emptyList(),
        )

        whenn()
        fixture.call { getFreeSlots(query) }

        then()
        assertEquals(HttpMethod.Post, fixture.request?.method)
        assertEquals("/api/appointments/slots", fixture.request?.url?.encodedPath)
        assertEquals(
            FreeSlotsRequestRemote(
                businessId = businessId,
                employeeId = employeeId,
                services = listOf(RequestedServiceRemote(serviceId, 2)),
                from = LocalDate(2026, 11, 23),
                to = LocalDate(2026, 11, 29),
            ),
            fixture.freeSlotsRequest(),
        )
    }

    @Test
    fun `reads the free slots of each day, keeping days with none`() = runUnitTest {
        given()
        val fixture = Fixture()
        val start = Instant.parse("2026-11-24T12:30:00Z")
        val end = Instant.parse("2026-11-24T13:30:00Z")
        fixture.body = dataSerializer.encodeToByteArray(
            ListSerializer(FreeSlotsDayRemote.serializer()),
            listOf(
                FreeSlotsDayRemote(LocalDate(2026, 11, 23), emptyList()),
                FreeSlotsDayRemote(LocalDate(2026, 11, 24), listOf(FreeSlotRemote(start, end))),
            ),
        )
        val query = FreeSlotsQuery(
            businessId = Uuid.random(),
            employeeId = Uuid.random(),
            services = listOf(RequestedService(Uuid.random(), 1)),
            from = LocalDate(2026, 11, 23),
            to = LocalDate(2026, 11, 24),
        )

        whenn()
        val result = fixture.call { getFreeSlots(query) }

        then()
        assertEquals(
            listOf(
                FreeSlotsDay(LocalDate(2026, 11, 23), emptyList()),
                FreeSlotsDay(LocalDate(2026, 11, 24), listOf(FreeSlot(start, end))),
            ),
            result,
        )
    }
}
