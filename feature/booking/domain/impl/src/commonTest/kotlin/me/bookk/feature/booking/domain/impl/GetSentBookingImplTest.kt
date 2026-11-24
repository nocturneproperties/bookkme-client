package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking.BookedService
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetSentBookingImplTest {

    private val businessId = Uuid.random()
    private val specialistId = Uuid.random()
    private val startsAt = Instant.parse("2026-11-24T12:30:00Z")
    private val request = SentBookingRequest(businessId, specialistId, startsAt)
    private val haircut = serviceSnapshot("Haircut", 45.minutes)
    private val beardTrim = serviceSnapshot("Beard trim", 10.minutes)

    private class Fixture {
        val bookings = MutableStateFlow<List<UpcomingBooking>>(emptyList())
        val getUpcomingBookings = mock<GetUpcomingBookings> {
            every { flow() } returns bookings
        }
        val sut = GetSentBookingImpl(getUpcomingBookings)
    }

    @Test
    fun `emits the sent request with its time and services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val booking = upcomingBooking(services = listOf(haircut, beardTrim))
        fixture.bookings.value = listOf(booking)

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertEquals(
            SentBooking(
                id = booking.id,
                startsAt = localTime(startsAt),
                endsAt = localTime(startsAt + 55.minutes),
                services = listOf(BookedService("Haircut", 1), BookedService("Beard trim", 1)),
                status = SentBooking.Status.WAITING_FOR_CONFIRMATION,
            ),
            sent,
        )
    }

    @Test
    fun `counts a service booked more than once`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.bookings.value = listOf(upcomingBooking(services = listOf(beardTrim, beardTrim)))

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertEquals(listOf(BookedService("Beard trim", 2)), sent?.services)
    }

    @Test
    fun `emits nothing until the sent request is among the upcoming bookings`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertNull(sent)
    }

    @Test
    fun `ignores a booking at the same time with another specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.bookings.value = listOf(upcomingBooking(employeeId = Uuid.random()))

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertNull(sent)
    }

    @Test
    fun `ignores a booking at the same time at another business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.bookings.value = listOf(upcomingBooking(businessId = Uuid.random()))

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertNull(sent)
    }

    @Test
    fun `ignores a booking with the same specialist at another time`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.bookings.value = listOf(upcomingBooking(date = localTime(startsAt + 60.minutes)))

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertNull(sent)
    }

    @Test
    fun `reports the request confirmed once the business confirms it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.bookings.value = listOf(upcomingBooking(status = UpcomingBooking.Status.CONFIRMED))

        whenn()
        val sent = fixture.sut.flow(request).first()

        then()
        assertEquals(SentBooking.Status.CONFIRMED, sent?.status)
    }

    private fun upcomingBooking(
        businessId: Uuid = this.businessId,
        employeeId: Uuid = specialistId,
        date: LocalDateTime = localTime(startsAt),
        services: List<ServiceSnapshot> = listOf(haircut),
        status: UpcomingBooking.Status = UpcomingBooking.Status.WAITING_FOR_CONFIRMATION,
    ): UpcomingBooking {
        return UpcomingBooking(
            id = Uuid.random(),
            businessId = businessId,
            businessName = "Studio Lumen",
            services = services,
            employee = EmployeeSnapshot(employeeId, Uuid.random(), "Anna Kovalenko"),
            date = date,
            status = status,
        )
    }

    private fun serviceSnapshot(name: String, duration: Duration): ServiceSnapshot {
        return ServiceSnapshot(
            id = Uuid.random(),
            name = name,
            groupId = Uuid.random(),
            price = Money(10_000, Money.SupportedCurrency.UAH),
            duration = duration,
        )
    }

    private fun localTime(instant: Instant): LocalDateTime {
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
