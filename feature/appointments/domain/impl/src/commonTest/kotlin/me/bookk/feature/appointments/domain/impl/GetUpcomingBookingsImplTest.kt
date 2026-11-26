package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class GetUpcomingBookingsImplTest {

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val sut = GetUpcomingBookingsImpl(dataSource)

        fun stubCache(bookings: UpcomingBookings) {
            every { dataSource.observeAppointments(ClientBookingPeriod.UPCOMING) } returns
                flowOf(bookings.appointments)
            every { dataSource.observeRequests(ClientBookingPeriod.UPCOMING) } returns
                flowOf(bookings.requests)
        }

        fun stubRemote(bookings: UpcomingBookings) {
            everySuspend { dataSource.getUpcomingBookings() } returns bookings
            everySuspend { dataSource.getAppointmentIdsInDb(any(), any()) } returns emptyList()
            everySuspend { dataSource.getRequestIdsInDb(any(), any()) } returns emptyList()
            everySuspend { dataSource.deleteAppointmentsInDb(any()) } returns Unit
            everySuspend { dataSource.deleteRequestsInDb(any()) } returns Unit
            everySuspend { dataSource.saveAppointmentsInDb(any()) } returns Unit
            everySuspend { dataSource.saveRequestsInDb(any()) } returns Unit
        }
    }

    private val tuesday = LocalDateTime(2026, 11, 24, 14, 30)
    private val friday = LocalDateTime(2026, 11, 27, 11, 0)

    @Test
    fun `flow merges cached appointments and requests soonest first`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment().copy(date = friday)
        val request = stubAppointmentRequest()
            .copy(date = tuesday.toInstant(TimeZone.currentSystemDefault()))
        fixture.stubCache(UpcomingBookings(listOf(appointment), listOf(request)))

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(listOf(request.id, appointment.id), result.map { it.id })
    }

    @Test
    fun `flow marks appointments confirmed and requests waiting for confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment().copy(date = tuesday)
        val request = stubAppointmentRequest()
            .copy(date = friday.toInstant(TimeZone.currentSystemDefault()))
        fixture.stubCache(UpcomingBookings(listOf(appointment), listOf(request)))

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(
            listOf(
                UpcomingBooking.Status.CONFIRMED,
                UpcomingBooking.Status.WAITING_FOR_CONFIRMATION,
            ),
            result.map { it.status },
        )
    }

    @Test
    fun `flow takes request date in the device time zone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = stubAppointmentRequest()
            .copy(date = friday.toInstant(TimeZone.currentSystemDefault()))
        fixture.stubCache(UpcomingBookings(emptyList(), listOf(request)))

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(friday, result.single().date)
    }

    @Test
    fun `flow names each booking after the business it came with`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment().copy(businessName = "Studio Lumen")
        val request = stubAppointmentRequest().copy(businessName = "Nail Room")
        fixture.stubCache(UpcomingBookings(listOf(appointment), listOf(request)))

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(setOf("Studio Lumen", "Nail Room"), result.map { it.businessName }.toSet())
    }

    @Test
    fun `refresh returns the fetched bookings soonest first with business names`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment().copy(date = friday, businessName = "Studio")
        val request = stubAppointmentRequest()
            .copy(
                date = tuesday.toInstant(TimeZone.currentSystemDefault()),
                businessName = "Studio",
            )
        fixture.stubRemote(UpcomingBookings(listOf(appointment), listOf(request)))

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(listOf(request.id, appointment.id), result.map { it.id })
        assertEquals(listOf("Studio", "Studio"), result.map { it.businessName })
    }

    @Test
    fun `refresh deletes every cached upcoming booking missing from a short response`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val stale = Uuid.random()
            val bookings =
                UpcomingBookings(listOf(stubAppointment().copy(date = friday)), emptyList())
            fixture.stubRemote(bookings)
            everySuspend {
                fixture.dataSource.getAppointmentIdsInDb(ClientBookingPeriod.UPCOMING, DateBounds())
            } returns listOf(stale)

            whenn()
            fixture.sut.refresh()

            then()
            verifySuspend { fixture.dataSource.deleteAppointmentsInDb(listOf(stale)) }
        }

    @Test
    fun `refresh deletes stale upcoming bookings only up to the last one of a full response`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val requests = listOf(
                stubAppointmentRequest().copy(
                    date = tuesday.toInstant(TimeZone.currentSystemDefault()),
                ),
                stubAppointmentRequest().copy(
                    date = friday.toInstant(TimeZone.currentSystemDefault()),
                ),
            )
            fixture.stubRemote(UpcomingBookings(emptyList(), requests))

            whenn()
            fixture.sut.refresh()

            then()
            verifySuspend {
                fixture.dataSource.getRequestIdsInDb(
                    ClientBookingPeriod.UPCOMING,
                    DateBounds(to = friday.toInstant(TimeZone.currentSystemDefault())),
                )
            }
        }

    @Test
    fun `refresh saves the fetched bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val bookings = UpcomingBookings(listOf(stubAppointment()), listOf(stubAppointmentRequest()))
        fixture.stubRemote(bookings)

        whenn()
        fixture.sut.refresh()

        then()
        verifySuspend {
            fixture.dataSource.saveAppointmentsInDb(bookings.appointments)
            fixture.dataSource.saveRequestsInDb(bookings.requests)
        }
    }
}
