package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PageErrorCodes
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetClientBookingsImplTest {

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val sut = GetClientBookingsImpl(dataSource)

        fun stubCache(
            period: ClientBookingPeriod,
            appointments: List<Appointment>,
            requests: List<AppointmentRequest>,
        ) {
            every { dataSource.observeAppointments(period) } returns flowOf(appointments)
            every { dataSource.observeRequests(period) } returns flowOf(requests)
        }

        fun stubAppointments(
            period: ClientBookingPeriod,
            cursor: String?,
            page: Page<Appointment>,
        ) {
            everySuspend { dataSource.getOwnAppointments(period, cursor) } returns page
        }

        fun stubRequests(
            period: ClientBookingPeriod,
            cursor: String?,
            page: Page<AppointmentRequest>,
        ) {
            everySuspend { dataSource.getOwnRequests(period, cursor) } returns page
        }

        fun stubCacheWrites(
            staleAppointmentIds: List<Uuid> = emptyList(),
            staleRequestIds: List<Uuid> = emptyList(),
        ) {
            everySuspend { dataSource.getAppointmentIdsInDb(any(), any()) } returns
                staleAppointmentIds
            everySuspend { dataSource.getRequestIdsInDb(any(), any()) } returns staleRequestIds
            everySuspend { dataSource.deleteAppointmentsInDb(any()) } returns Unit
            everySuspend { dataSource.deleteRequestsInDb(any()) } returns Unit
            everySuspend { dataSource.saveAppointmentsInDb(any()) } returns Unit
            everySuspend { dataSource.saveRequestsInDb(any()) } returns Unit
        }
    }

    private val start = Instant.fromEpochMilliseconds(1_800_000_000_000)

    private fun at(hour: Int): Instant = start + hour.hours

    private fun appointmentAt(hour: Int): Appointment {
        val date = at(hour).toLocalDateTime(TimeZone.currentSystemDefault())
        return stubAppointment().copy(date = date)
    }

    private fun requestAt(hour: Int): AppointmentRequest {
        return stubAppointmentRequest().copy(date = at(hour))
    }

    private fun List<ClientBooking>.ids(): List<Uuid> {
        return map {
            when (it) {
                is ClientBooking.Visit -> it.appointment.id
                is ClientBooking.Request -> it.request.id
            }
        }
    }

    @Test
    fun `flow lists cached upcoming bookings soonest first`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointmentAt(hour = 5)
        val request = requestAt(hour = 2)
        fixture.stubCache(ClientBookingPeriod.UPCOMING, listOf(appointment), listOf(request))

        whenn()
        val result = fixture.sut.flow(ClientBookingPeriod.UPCOMING).first()

        then()
        assertEquals(listOf(request.id, appointment.id), result.ids())
    }

    @Test
    fun `flow lists cached past bookings newest first`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointmentAt(hour = 5)
        val request = requestAt(hour = 2)
        fixture.stubCache(ClientBookingPeriod.PAST, listOf(appointment), listOf(request))

        whenn()
        val result = fixture.sut.flow(ClientBookingPeriod.PAST).first()

        then()
        assertEquals(listOf(appointment.id, request.id), result.ids())
    }

    @Test
    fun `refresh returns both first pages merged with no next page when both end`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointmentAt(hour = 3)
        val request = requestAt(hour = 1)
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointment), null),
        )
        fixture.stubRequests(ClientBookingPeriod.UPCOMING, null, Page(listOf(request), null))
        fixture.stubCacheWrites()

        whenn()
        val result = fixture.sut.refresh(ClientBookingPeriod.UPCOMING)

        then()
        assertEquals(listOf(request.id, appointment.id), result.items.ids())
        assertNull(result.nextCursor)
    }

    @Test
    fun `refresh saves both first pages`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointmentAt(hour = 3)
        val request = requestAt(hour = 1)
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointment), null),
        )
        fixture.stubRequests(ClientBookingPeriod.UPCOMING, null, Page(listOf(request), null))
        fixture.stubCacheWrites()

        whenn()
        fixture.sut.refresh(ClientBookingPeriod.UPCOMING)

        then()
        verifySuspend {
            fixture.dataSource.saveAppointmentsInDb(listOf(appointment))
            fixture.dataSource.saveRequestsInDb(listOf(request))
        }
    }

    @Test
    fun `refresh deletes cached bookings missing from lists that both ended`() = runUnitTest {
        given()
        val fixture = Fixture()
        val kept = appointmentAt(hour = 3)
        val staleAppointment = Uuid.random()
        val staleRequest = Uuid.random()
        fixture.stubAppointments(ClientBookingPeriod.UPCOMING, null, Page(listOf(kept), null))
        fixture.stubRequests(ClientBookingPeriod.UPCOMING, null, Page(emptyList(), null))
        fixture.stubCacheWrites()
        everySuspend {
            fixture.dataSource.getAppointmentIdsInDb(ClientBookingPeriod.UPCOMING, DateBounds())
        } returns listOf(kept.id, staleAppointment)
        everySuspend {
            fixture.dataSource.getRequestIdsInDb(ClientBookingPeriod.UPCOMING, DateBounds())
        } returns listOf(staleRequest)

        whenn()
        fixture.sut.refresh(ClientBookingPeriod.UPCOMING)

        then()
        verifySuspend {
            fixture.dataSource.deleteAppointmentsInDb(listOf(staleAppointment))
            fixture.dataSource.deleteRequestsInDb(listOf(staleRequest))
        }
    }

    @Test
    fun `refresh holds back bookings after the last date of a list with more pages`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val early = appointmentAt(hour = 1)
            val late = appointmentAt(hour = 9)
            val request = requestAt(hour = 4)
            fixture.stubAppointments(
                ClientBookingPeriod.UPCOMING,
                null,
                Page(listOf(early, late), null),
            )
            fixture.stubRequests(ClientBookingPeriod.UPCOMING, null, Page(listOf(request), "r2"))
            fixture.stubCacheWrites()

            whenn()
            val result = fixture.sut.refresh(ClientBookingPeriod.UPCOMING)

            then()
            assertEquals(listOf(early.id, request.id), result.items.ids())
            verifySuspend { fixture.dataSource.saveAppointmentsInDb(listOf(early)) }
        }

    @Test
    fun `refresh deletes stale bookings only up to the last date both lists cover`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointmentAt(hour = 9)), null),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(requestAt(hour = 4)), "r2"),
        )
        fixture.stubCacheWrites()

        whenn()
        fixture.sut.refresh(ClientBookingPeriod.UPCOMING)

        then()
        verifySuspend {
            fixture.dataSource.getAppointmentIdsInDb(
                ClientBookingPeriod.UPCOMING,
                DateBounds(to = at(hour = 4)),
            )
        }
    }

    @Test
    fun `load more fetches the held back list again and the other from its next page`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val late = appointmentAt(hour = 9)
            fixture.stubAppointments(ClientBookingPeriod.UPCOMING, null, Page(listOf(late), null))
            fixture.stubRequests(
                ClientBookingPeriod.UPCOMING,
                null,
                Page(listOf(requestAt(hour = 4)), "r2"),
            )
            val laterRequest = requestAt(hour = 12)
            fixture.stubRequests(
                ClientBookingPeriod.UPCOMING,
                "r2",
                Page(listOf(laterRequest), null),
            )
            fixture.stubCacheWrites()
            val cursor =
                requireNotNull(fixture.sut.refresh(ClientBookingPeriod.UPCOMING).nextCursor)

            whenn()
            val result = fixture.sut.loadMore(ClientBookingPeriod.UPCOMING, cursor)

            then()
            assertEquals(listOf(late.id, laterRequest.id), result.items.ids())
            assertNull(result.nextCursor)
        }

    @Test
    fun `load more deletes stale bookings only after the dates already covered`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointmentAt(hour = 9)), null),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(requestAt(hour = 4)), "r2"),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            "r2",
            Page(listOf(requestAt(hour = 6)), "r3"),
        )
        fixture.stubCacheWrites()
        val cursor = requireNotNull(fixture.sut.refresh(ClientBookingPeriod.UPCOMING).nextCursor)

        whenn()
        fixture.sut.loadMore(ClientBookingPeriod.UPCOMING, cursor)

        then()
        verifySuspend {
            fixture.dataSource.getRequestIdsInDb(
                ClientBookingPeriod.UPCOMING,
                DateBounds(from = at(hour = 4) + 1.milliseconds, to = at(hour = 6)),
            )
        }
    }

    @Test
    fun `past pages cover dates downwards from the newest`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubAppointments(
            ClientBookingPeriod.PAST,
            null,
            Page(listOf(appointmentAt(hour = 9), appointmentAt(hour = 1)), null),
        )
        fixture.stubRequests(
            ClientBookingPeriod.PAST,
            null,
            Page(listOf(requestAt(hour = 5)), "r2"),
        )
        fixture.stubRequests(
            ClientBookingPeriod.PAST,
            "r2",
            Page(listOf(requestAt(hour = 3)), "r3"),
        )
        fixture.stubCacheWrites()
        val cursor = requireNotNull(fixture.sut.refresh(ClientBookingPeriod.PAST).nextCursor)

        whenn()
        fixture.sut.loadMore(ClientBookingPeriod.PAST, cursor)

        then()
        verifySuspend {
            fixture.dataSource.getAppointmentIdsInDb(
                ClientBookingPeriod.PAST,
                DateBounds(from = at(hour = 3), to = at(hour = 5) - 1.milliseconds),
            )
        }
    }

    @Test
    fun `load more does not fetch a list that already ended`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointmentAt(hour = 1)), null),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(requestAt(hour = 4)), "r2"),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            "r2",
            Page(listOf(requestAt(hour = 6)), null),
        )
        fixture.stubCacheWrites()
        val cursor = requireNotNull(fixture.sut.refresh(ClientBookingPeriod.UPCOMING).nextCursor)

        whenn()
        fixture.sut.loadMore(ClientBookingPeriod.UPCOMING, cursor)

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.getOwnAppointments(ClientBookingPeriod.UPCOMING, any())
        }
    }

    @Test
    fun `load more starts over when the backend no longer accepts the cursor`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointmentAt(hour = 1)
        fixture.stubAppointments(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(appointment), null),
        )
        fixture.stubRequests(
            ClientBookingPeriod.UPCOMING,
            null,
            Page(listOf(requestAt(hour = 4)), "r2"),
        )
        fixture.stubCacheWrites()
        val cursor = requireNotNull(fixture.sut.refresh(ClientBookingPeriod.UPCOMING).nextCursor)
        everySuspend {
            fixture.dataSource.getOwnRequests(ClientBookingPeriod.UPCOMING, "r2")
        } throws Error.BusinessError(PageErrorCodes.CURSOR_INVALID, "")

        whenn()
        val result = fixture.sut.loadMore(ClientBookingPeriod.UPCOMING, cursor)

        then()
        assertEquals(appointment.id, result.items.ids().first())
    }

    @Test
    fun `load more starts over when the cursor cannot be read`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = requestAt(hour = 4)
        fixture.stubAppointments(ClientBookingPeriod.UPCOMING, null, Page(emptyList(), null))
        fixture.stubRequests(ClientBookingPeriod.UPCOMING, null, Page(listOf(request), null))
        fixture.stubCacheWrites()

        whenn()
        val result = fixture.sut.loadMore(ClientBookingPeriod.UPCOMING, "unreadable")

        then()
        assertEquals(listOf(request.id), result.items.ids())
    }
}
