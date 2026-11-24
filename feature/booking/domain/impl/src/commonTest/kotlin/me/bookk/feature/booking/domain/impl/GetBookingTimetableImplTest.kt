package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetBookingTimetableImplTest {

    private val businessId = Uuid.random()
    private val kyiv = TimeZone.of("Europe/Kyiv")
    private val schedule = WorkingSchedule(
        days = DayOfWeek.entries.associateWith {
            DayOfWeekSchedule.default(it).copy(isActive = it != DayOfWeek.SUNDAY)
        },
        dayOffs = listOf(DayOffRange(LocalDate(2026, 11, 25), LocalDate(2026, 11, 26))),
    )
    private val studio = stubBusiness(businessId, "Studio Lumen", kyiv, schedule)
    private val andrii = stubEmployee(businessId, "Andrii", "Koval", emptyList())
    private val maria = stubEmployee(businessId, "Maria", "Shevchuk", emptyList())

    private inner class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val savedBusinesses = MutableStateFlow(listOf(studio))
        val savedEmployees = MutableStateFlow(listOf(andrii, maria))
        val getBusinesses = mock<GetBusinesses> {
            every { flow(setOf(businessId)) } returns savedBusinesses
            everySuspend { refresh(setOf(businessId)) } returns listOf(studio)
        }
        val getBookableEmployees = mock<GetBookableEmployees> {
            every { flow(businessId) } returns savedEmployees
            everySuspend { refresh(businessId) } returns listOf(andrii, maria)
        }
        val sut = GetBookingTimetableImpl(dataSource, getBusinesses, getBookableEmployees)

        fun saveBusinesses(vararg businesses: Business) {
            savedBusinesses.value = businesses.toList()
        }
    }

    private fun BookableEmployee.toExpectedSpecialist(): BookingSpecialist {
        return BookingSpecialist(id = id, name = name, lastName = lastName)
    }

    @Test
    fun `names the business, its time zone and the picked specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = maria.id)

        whenn()
        val timetable = fixture.sut.flow(businessId).first()

        then()
        assertEquals(
            BookingTimetable(
                businessName = "Studio Lumen",
                timeZone = kyiv,
                specialist = maria.toExpectedSpecialist(),
                closedWeekdays = setOf(DayOfWeek.SUNDAY),
                daysOff = listOf(LocalDate(2026, 11, 25)..LocalDate(2026, 11, 26)),
            ),
            timetable,
        )
    }

    @Test
    fun `tells the business is closed on its days off and inactive weekdays`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = maria.id)

        whenn()
        val timetable = fixture.sut.flow(businessId).first()

        then()
        val week = (23..29).map { LocalDate(2026, 11, it) }
        assertEquals(
            listOf(false, false, true, true, false, false, true),
            week.map { timetable.isClosedOn(it) },
        )
    }

    @Test
    fun `refreshes the business and its bookable specialists`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend {
            fixture.getBusinesses.refresh(setOf(businessId))
            fixture.getBookableEmployees.refresh(businessId)
        }
    }

    @Test
    fun `throws business not found when the business is not returned`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinesses.refresh(setOf(businessId)) } returns emptyList()

        whenn()
        val call = suspend { fixture.sut.refresh(businessId) }

        then()
        assertFailsWith<GetBookingTimetable.Error.BusinessNotFound> { call() }
    }

    @Test
    fun `has no specialist when the picked one is no longer bookable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = Uuid.random())

        whenn()
        val timetable = fixture.sut.flow(businessId).first()

        then()
        assertEquals(null, timetable.specialist)
    }

    @Test
    fun `waits for the business to be saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = andrii.id)
        fixture.saveBusinesses()

        val timetables = mutableListOf<BookingTimetable>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.flow(businessId).toList(timetables)
        }

        whenn()
        fixture.saveBusinesses(studio)

        then()
        assertEquals(listOf("Studio Lumen"), timetables.map { it.businessName })
    }

    @Test
    fun `follows the specialist picked later`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = andrii.id)

        whenn()
        fixture.dataSource.select(businessId, specialistId = maria.id)

        then()
        assertEquals(
            maria.toExpectedSpecialist(),
            fixture.sut.flow(businessId).first().specialist,
        )
    }
}
