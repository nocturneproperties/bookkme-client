package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetFreeSlots
import me.bookk.feature.appointments.domain.api.entity.FreeSlot
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.domain.api.GetBookingSlots.Error
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetBookingSlotsImplTest {

    private val businessId = Uuid.random()
    private val specialistId = Uuid.random()
    private val haircutId = Uuid.random()
    private val beardTrimId = Uuid.random()
    private val monday = LocalDate(2026, 11, 23)
    private val sunday = LocalDate(2026, 11, 29)
    private val timetable = BookingTimetable(
        businessName = "Studio Lumen",
        timeZone = TimeZone.of("Europe/Kyiv"),
        specialist = null,
        closedWeekdays = setOf(DayOfWeek.SUNDAY),
        daysOff = listOf(LocalDate(2026, 11, 25)..LocalDate(2026, 11, 25)),
    )

    private inner class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val getFreeSlots = mock<GetFreeSlots>()
        val sut = GetBookingSlotsImpl(dataSource, getFreeSlots)

        fun respond(vararg days: FreeSlotsDay) {
            everySuspend { getFreeSlots(any()) } returns days.toList()
        }

        fun fail(error: Throwable) {
            everySuspend { getFreeSlots(any()) } throws error
        }

        suspend fun loadWeek(): List<BookingDay> {
            return sut(businessId, timetable, monday, sunday)
        }
    }

    @Test
    fun `asks for the slots of the picked specialist and services in the range`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(
            businessId,
            haircutId to 1,
            beardTrimId to 2,
            specialistId = specialistId,
        )
        fixture.respond()

        whenn()
        fixture.loadWeek()

        then()
        verifySuspend {
            fixture.getFreeSlots(
                FreeSlotsQuery(
                    businessId = businessId,
                    employeeId = specialistId,
                    services = listOf(
                        RequestedService(haircutId, 1),
                        RequestedService(beardTrimId, 2),
                    ),
                    from = monday,
                    to = sunday,
                ),
            )
        }
    }

    @Test
    fun `tells the time of each slot in the business's time zone`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)
        val start = Instant.parse("2026-11-24T12:30:00Z")
        fixture.respond(
            FreeSlotsDay(
                date = LocalDate(2026, 11, 24),
                slots = listOf(FreeSlot(start, Instant.parse("2026-11-24T13:15:00Z"))),
            ),
        )

        whenn()
        val days = fixture.loadWeek()

        then()
        assertEquals(
            listOf(
                BookingDay(
                    date = LocalDate(2026, 11, 24),
                    slots = listOf(BookingSlot(start, LocalTime(14, 30))),
                    isBusinessClosed = false,
                ),
            ),
            days,
        )
    }

    @Test
    fun `marks the days the business is closed on`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)
        fixture.respond(
            FreeSlotsDay(LocalDate(2026, 11, 24), emptyList()),
            FreeSlotsDay(LocalDate(2026, 11, 25), emptyList()),
            FreeSlotsDay(sunday, emptyList()),
        )

        whenn()
        val days = fixture.loadWeek()

        then()
        assertEquals(listOf(false, true, true), days.map { it.isBusinessClosed })
    }

    @Test
    fun `throws booking unavailable when the business takes no appointments`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)
        fixture.fail(GetFreeSlots.Error.AppointmentsNotEnabled())

        whenn()
        val call = suspend { fixture.loadWeek() }

        then()
        assertFailsWith<Error.BookingUnavailable> { call() }
    }

    @Test
    fun `throws selection invalid when the picked services can no longer be booked`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)
            fixture.fail(GetFreeSlots.Error.InvalidServiceSelection())

            whenn()
            val call = suspend { fixture.loadWeek() }

            then()
            assertFailsWith<Error.SelectionInvalid> { call() }
        }

    @Test
    fun `throws invalid range when the backend rejects the dates`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)
        fixture.fail(GetFreeSlots.Error.InvalidRange())

        whenn()
        val call = suspend { fixture.loadWeek() }

        then()
        assertFailsWith<Error.InvalidRange> { call() }
    }

    @Test
    fun `throws selection invalid without asking when no specialist is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1)

        whenn()
        val call = suspend { fixture.loadWeek() }

        then()
        assertFailsWith<Error.SelectionInvalid> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getFreeSlots(any()) }
    }

    @Test
    fun `throws selection invalid without asking when no service is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = specialistId)

        whenn()
        val call = suspend { fixture.loadWeek() }

        then()
        assertFailsWith<Error.SelectionInvalid> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getFreeSlots(any()) }
    }
}
