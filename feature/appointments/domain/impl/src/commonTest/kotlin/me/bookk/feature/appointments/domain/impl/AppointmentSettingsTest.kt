package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Day offs moved from the top level of the settings into the working schedule, which the
 * business service now owns — these cover the workday/worktime checks that read them.
 */
class AppointmentSettingsTest {

    private val timeZone = TimeZone.UTC

    // 2026-08-05 is a Wednesday.
    private val wednesday = LocalDate(2026, 8, 5)

    private fun settings(
        activeDays: Set<DayOfWeek> = setOf(DayOfWeek.WEDNESDAY),
        workingTime: List<WorkHour> = listOf(WorkHour(LocalTime(9, 0), LocalTime(17, 0))),
        dayOffs: List<DayOffRange> = emptyList()
    ) = AppointmentSettings(
        id = Uuid.random(),
        businessId = Uuid.random(),
        timeZone = timeZone,
        schedule = WorkingSchedule(
            days = DayOfWeek.entries.associateWith { day ->
                DayOfWeekSchedule(
                    dayOfWeek = day,
                    workingTime = if (day in activeDays) workingTime else emptyList(),
                    isActive = day in activeDays
                )
            },
            dayOffs = dayOffs
        ),
        automaticApproval = false,
        inBetweenBreakInMinutes = 10,
        appointmentNote = "",
        permissions = ResourcePermission()
    )

    private fun instantAt(date: LocalDate, time: LocalTime) =
        LocalDateTime(date, time).toInstant(timeZone)

    @Test
    fun `isInWorkday returns true for an active day with no day offs`() = runUnitTest {
        given()
        val sut = settings()

        whenn()
        val result = sut.isInWorkday(instantAt(wednesday, LocalTime(10, 0)))

        then()
        assertTrue(result)
    }

    @Test
    fun `isInWorkday returns false for an inactive day`() = runUnitTest {
        given()
        val sut = settings(activeDays = setOf(DayOfWeek.MONDAY))

        whenn()
        val result = sut.isInWorkday(instantAt(wednesday, LocalTime(10, 0)))

        then()
        assertFalse(result)
    }

    @Test
    fun `isInWorkday returns false when the date falls inside a day off range`() = runUnitTest {
        given()
        val sut = settings(
            dayOffs = listOf(DayOffRange(start = LocalDate(2026, 8, 3), end = LocalDate(2026, 8, 7)))
        )

        whenn()
        val result = sut.isInWorkday(instantAt(wednesday, LocalTime(10, 0)))

        then()
        assertFalse(result)
    }

    @Test
    fun `isInWorkday returns false on the day off boundary dates`() = runUnitTest {
        given()
        val sut = settings(
            dayOffs = listOf(DayOffRange(start = wednesday, end = wednesday))
        )

        whenn()
        val result = sut.isInWorkday(instantAt(wednesday, LocalTime(10, 0)))

        then()
        assertFalse(result)
    }

    @Test
    fun `isInWorkday returns true when day offs cover a different range`() = runUnitTest {
        given()
        val sut = settings(
            dayOffs = listOf(DayOffRange(start = LocalDate(2026, 9, 1), end = LocalDate(2026, 9, 5)))
        )

        whenn()
        val result = sut.isInWorkday(instantAt(wednesday, LocalTime(10, 0)))

        then()
        assertTrue(result)
    }

    @Test
    fun `isInWorktime returns true inside a work hour block`() = runUnitTest {
        given()
        val sut = settings()

        whenn()
        val result = sut.isInWorktime(instantAt(wednesday, LocalTime(12, 0)))

        then()
        assertTrue(result)
    }

    @Test
    fun `isInWorktime returns false outside every work hour block`() = runUnitTest {
        given()
        val sut = settings()

        whenn()
        val result = sut.isInWorktime(instantAt(wednesday, LocalTime(18, 0)))

        then()
        assertFalse(result)
    }

    @Test
    fun `isInWorktime returns false for a day without work hours`() = runUnitTest {
        given()
        val sut = settings(activeDays = setOf(DayOfWeek.MONDAY))

        whenn()
        val result = sut.isInWorktime(instantAt(wednesday, LocalTime(12, 0)))

        then()
        assertFalse(result)
    }
}
