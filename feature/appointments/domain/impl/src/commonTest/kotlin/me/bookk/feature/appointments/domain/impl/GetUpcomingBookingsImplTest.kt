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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetUpcomingBookingsImplTest {

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val getBusinesses = mock<GetBusinesses>()
        val sut = GetUpcomingBookingsImpl(dataSource, getBusinesses)

        fun stubCache(bookings: UpcomingBookings, businesses: List<Business>) {
            every { dataSource.observeUpcomingAppointments() } returns flowOf(bookings.appointments)
            every { dataSource.observeUpcomingRequests() } returns flowOf(bookings.requests)
            every { getBusinesses.flow(bookings.businessIds) } returns flowOf(businesses)
        }

        fun stubRemote(bookings: UpcomingBookings, businesses: List<Business>) {
            everySuspend { dataSource.getUpcomingBookings() } returns bookings
            everySuspend { getBusinesses.refresh(bookings.businessIds) } returns businesses
            everySuspend { dataSource.saveUpcomingBookingsInDb(any()) } returns Unit
        }
    }

    private val tuesday = LocalDateTime(2026, 11, 24, 14, 30)
    private val friday = LocalDateTime(2026, 11, 27, 11, 0)

    private fun business(id: Uuid, name: String) = stubBusiness(id = id).copy(name = name)

    @Test
    fun `flow merges cached appointments and requests soonest first`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointment = stubAppointment(businessId = businessId).copy(date = friday)
        val request = stubAppointmentRequest(businessId = businessId)
            .copy(date = tuesday.toInstant(TimeZone.currentSystemDefault()))
        fixture.stubCache(
            UpcomingBookings(listOf(appointment), listOf(request)),
            listOf(business(businessId, "Studio")),
        )

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
        fixture.stubCache(UpcomingBookings(listOf(appointment), listOf(request)), emptyList())

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
        fixture.stubCache(UpcomingBookings(emptyList(), listOf(request)), emptyList())

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(friday, result.single().date)
    }

    @Test
    fun `flow names each booking after its cached business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointment = stubAppointment(businessId = businessId)
        fixture.stubCache(
            UpcomingBookings(listOf(appointment), emptyList()),
            listOf(business(businessId, "Studio Lumen")),
        )

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals("Studio Lumen", result.single().businessName)
    }

    @Test
    fun `flow leaves the business name empty while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(UpcomingBookings(listOf(stubAppointment()), emptyList()), emptyList())

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals("", result.single().businessName)
    }

    @Test
    fun `refresh refreshes every booked business before saving the bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = Uuid.random()
        val second = Uuid.random()
        val bookings = UpcomingBookings(
            listOf(stubAppointment(businessId = first)),
            listOf(stubAppointmentRequest(businessId = second)),
        )
        fixture.stubRemote(
            bookings,
            listOf(business(first, "Studio"), business(second, "Nail Room")),
        )

        whenn()
        fixture.sut.refresh()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.getBusinesses.refresh(setOf(first, second))
            fixture.dataSource.saveUpcomingBookingsInDb(bookings)
        }
    }

    @Test
    fun `refresh returns the fetched bookings soonest first with business names`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointment = stubAppointment(businessId = businessId).copy(date = friday)
        val request = stubAppointmentRequest(businessId = businessId)
            .copy(date = tuesday.toInstant(TimeZone.currentSystemDefault()))
        fixture.stubRemote(
            UpcomingBookings(listOf(appointment), listOf(request)),
            listOf(business(businessId, "Studio")),
        )

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(listOf(request.id, appointment.id), result.map { it.id })
        assertEquals(listOf("Studio", "Studio"), result.map { it.businessName })
    }

    @Test
    fun `refresh keeps the cache when the businesses cannot be fetched`() = runUnitTest {
        given()
        val fixture = Fixture()
        val bookings = UpcomingBookings(listOf(stubAppointment()), emptyList())
        everySuspend { fixture.dataSource.getUpcomingBookings() } returns bookings
        everySuspend { fixture.getBusinesses.refresh(any()) } throws IllegalStateException()

        whenn()
        assertFailsWith<IllegalStateException> { fixture.sut.refresh() }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveUpcomingBookingsInDb(any()) }
    }
}
