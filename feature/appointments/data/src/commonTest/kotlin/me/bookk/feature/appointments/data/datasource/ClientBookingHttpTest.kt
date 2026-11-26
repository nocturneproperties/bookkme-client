package me.bookk.feature.appointments.data.datasource

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import me.bookk.core.data.dataSerializer
import me.bookk.core.domain.pagination.Page
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.BookingInfoDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentPageRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestPageRemote
import me.bookk.feature.appointments.data.remote.model.BookingInfoRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsDayRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsRequestRemote
import me.bookk.feature.appointments.data.remote.model.RequestedServiceRemote
import me.bookk.feature.appointments.data.remote.model.SavedBusinessRemote
import me.bookk.feature.appointments.data.remote.model.UpcomingBookingsRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.FreeSlot
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.DateBounds
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
        val appointmentDao = mock<AppointmentDao>()
        val appointmentRequestDao = mock<AppointmentRequestDao>()
        val sut = CommonClientBookingDataSource(
            client,
            appointmentDao,
            appointmentRequestDao,
            mock<SavedBusinessDao>(),
            mock<BookingInfoDao>(),
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

    @Test
    fun `fetches the booking info of a business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.body = dataSerializer.encodeToByteArray(
            BookingInfoRemote.serializer(),
            BookingInfoRemote(businessId, "Please come 5 minutes early."),
        )

        whenn()
        val result = fixture.call { getBookingInfo(businessId) }

        then()
        assertEquals(HttpMethod.Get, fixture.request?.method)
        assertEquals(
            "/api/appointments/booking-info/$businessId",
            fixture.request?.url?.encodedPath,
        )
        assertEquals(BookingInfo(businessId, "Please come 5 minutes early."), result)
    }

    @Test
    fun `reads booking info without a note as an empty note`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.body = dataSerializer.encodeToByteArray(
            BookingInfoRemote.serializer(),
            BookingInfoRemote(businessId, ""),
        )

        whenn()
        val result = fixture.call { getBookingInfo(businessId) }

        then()
        assertEquals(BookingInfo(businessId, ""), result)
    }

    @Test
    fun `fetches a page of own upcoming appointments that are still scheduled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub(date = LocalDateTime(2026, 11, 24, 14, 30))
        fixture.body = dataSerializer.encodeToByteArray(
            AppointmentPageRemote.serializer(),
            AppointmentPageRemote(listOf(appointment.toRemote()), nextCursor = "after-a"),
        )

        whenn()
        val result = fixture.call { getOwnAppointments(ClientBookingPeriod.UPCOMING, "from-a") }

        then()
        assertEquals("/api/appointments/mine", fixture.request?.url?.encodedPath)
        assertEquals(
            "cursor=from-a&period=UPCOMING&status=SCHEDULED",
            fixture.request?.url?.encodedQuery,
        )
        assertEquals(Page(listOf(appointment), "after-a"), result)
    }

    @Test
    fun `fetches own past appointments by their finished statuses`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.call { getOwnAppointments(ClientBookingPeriod.PAST, cursor = null) }

        then()
        assertEquals(
            "status=COMPLETED&status=CANCELLED&status=NO_SHOW&status=UNRESOLVED",
            fixture.request?.url?.encodedQuery,
        )
    }

    @Test
    fun `fetches a page of own upcoming requests that are still pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.body = dataSerializer.encodeToByteArray(
            AppointmentRequestPageRemote.serializer(),
            AppointmentRequestPageRemote(emptyList(), nextCursor = "after-r"),
        )

        whenn()
        val result = fixture.call { getOwnRequests(ClientBookingPeriod.UPCOMING, cursor = null) }

        then()
        assertEquals("/api/appointments/request/mine", fixture.request?.url?.encodedPath)
        assertEquals("period=UPCOMING&status=PENDING", fixture.request?.url?.encodedQuery)
        assertEquals(Page(emptyList(), "after-r"), result)
    }

    @Test
    fun `fetches own past requests by their answered statuses`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.call { getOwnRequests(ClientBookingPeriod.PAST, cursor = "from-r") }

        then()
        assertEquals(
            "cursor=from-r&status=DECLINED&status=CANCELLED",
            fixture.request?.url?.encodedQuery,
        )
    }

    @Test
    fun `reads cached past appointments by the statuses the past page asks for`() = runUnitTest {
        given()
        val fixture = Fixture()
        val statuses = listOf("COMPLETED", "CANCELLED", "NO_SHOW", "UNRESOLVED")
        every { fixture.appointmentDao.observeByStatuses(statuses) } returns flowOf(emptyList())

        whenn()
        val result = fixture.call { observeAppointments(ClientBookingPeriod.PAST).first() }

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `reads cached upcoming request ids within the dates`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val from = Instant.fromEpochMilliseconds(1_800_000_000_000)
        everySuspend {
            fixture.appointmentRequestDao.getIdsByStatusesBetween(listOf("PENDING"), from, null)
        } returns listOf(id)

        whenn()
        val result = fixture.call {
            getRequestIdsInDb(ClientBookingPeriod.UPCOMING, DateBounds(from = from))
        }

        then()
        assertEquals(listOf(id), result)
    }
}
