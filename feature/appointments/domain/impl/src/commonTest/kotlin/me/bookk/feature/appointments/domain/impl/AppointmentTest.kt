package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class AppointmentTest {

    private val zone = TimeZone.UTC
    private val start = LocalDateTime(2026, 10, 3, 10, 0)
    private val beforeStart = LocalDateTime(2026, 10, 3, 9, 59)

    private fun appointment(status: AppointmentStatus) =
        Appointment.stub(date = start).copy(status = status)

    @Test
    fun `only a started scheduled appointment can be completed`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val completable = statuses.filter {
            appointment(it).canBeCompleted(now = start.toInstant(zone), timeZone = zone)
        }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED), completable)
    }

    @Test
    fun `scheduled appointment cannot be completed before it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val completable = appointment.canBeCompleted(
            now = beforeStart.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, completable)
    }

    @Test
    fun `started scheduled or completed appointment can be marked as no-show`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val markable = statuses.filter {
            appointment(it).canBeMarkedNoShow(now = start.toInstant(zone), timeZone = zone)
        }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED), markable)
    }

    @Test
    fun `appointment cannot be marked as no-show before it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val markable = appointment.canBeMarkedNoShow(
            now = beforeStart.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, markable)
    }

    @Test
    fun `only a scheduled appointment that has not started can be rescheduled`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val reschedulable = statuses.filter {
            appointment(it).canBeRescheduled(now = beforeStart.toInstant(zone), timeZone = zone)
        }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED), reschedulable)
    }

    @Test
    fun `scheduled appointment cannot be rescheduled once it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val reschedulable = appointment.canBeRescheduled(
            now = start.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, reschedulable)
    }

    @Test
    fun `scheduled appointment can be completed until seven days after it ends`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val beforeCutoff = LocalDateTime(2026, 10, 10, 10, 29, 59)

        whenn()
        val completable = appointment.canBeCompleted(
            now = beforeCutoff.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(true, completable)
    }

    @Test
    fun `scheduled appointment can be completed exactly seven days after it ends`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val cutoff = LocalDateTime(2026, 10, 10, 10, 30)

        whenn()
        val completable = appointment.canBeCompleted(now = cutoff.toInstant(zone), timeZone = zone)

        then()
        assertEquals(true, completable)
    }

    @Test
    fun `scheduled appointment cannot be completed after its edit window closes`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val afterCutoff = LocalDateTime(2026, 10, 10, 10, 30, 1)

        whenn()
        val completable = appointment.canBeCompleted(
            now = afterCutoff.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, completable)
    }

    @Test
    fun `completion edit window includes the duration of all booked services`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED).copy(
            services = listOf(
                ServiceSnapshot.stub().copy(duration = 30.minutes),
                ServiceSnapshot.stub().copy(duration = 45.minutes),
            ),
        )
        val cutoff = LocalDateTime(2026, 10, 10, 11, 15)

        whenn()
        val completable = appointment.canBeCompleted(now = cutoff.toInstant(zone), timeZone = zone)

        then()
        assertEquals(true, completable)
    }

    @Test
    fun `completion edit window closes after the combined booked service duration`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED).copy(
            services = listOf(
                ServiceSnapshot.stub().copy(duration = 30.minutes),
                ServiceSnapshot.stub().copy(duration = 45.minutes),
            ),
        )
        val afterCutoff = LocalDateTime(2026, 10, 10, 11, 15, 1)

        whenn()
        val completable = appointment.canBeCompleted(
            now = afterCutoff.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, completable)
    }

    @Test
    fun `scheduled and completed appointments can be marked as no-show at the edit cutoff`() =
        runUnitTest {
            given()
            val cutoff = LocalDateTime(2026, 10, 10, 10, 30)

            whenn()
            val markable = AppointmentStatus.entries.filter {
                appointment(it).canBeMarkedNoShow(now = cutoff.toInstant(zone), timeZone = zone)
            }

            then()
            assertEquals(listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED), markable)
        }

    @Test
    fun `appointments cannot be marked as no-show after their edit window closes`() = runUnitTest {
        given()
        val afterCutoff = LocalDateTime(2026, 10, 10, 10, 30, 1)

        whenn()
        val markable = AppointmentStatus.entries.filter {
            appointment(it).canBeMarkedNoShow(now = afterCutoff.toInstant(zone), timeZone = zone)
        }

        then()
        assertEquals(emptyList(), markable)
    }

    @Test
    fun `no-show edit window includes the duration of all booked services`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.COMPLETED).copy(
            services = listOf(
                ServiceSnapshot.stub().copy(duration = 30.minutes),
                ServiceSnapshot.stub().copy(duration = 45.minutes),
            ),
        )
        val cutoff = LocalDateTime(2026, 10, 10, 11, 15)

        whenn()
        val markable = appointment.canBeMarkedNoShow(now = cutoff.toInstant(zone), timeZone = zone)

        then()
        assertEquals(true, markable)
    }

    @Test
    fun `unresolved appointment cannot be completed marked no-show or rescheduled`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.UNRESOLVED)

        whenn()
        val times = listOf(beforeStart, start, LocalDateTime(2026, 10, 11, 10, 0))
        val availability = times.map { now ->
            listOf(
                appointment.canBeCompleted(now.toInstant(zone), zone),
                appointment.canBeMarkedNoShow(now.toInstant(zone), zone),
                appointment.canBeRescheduled(now.toInstant(zone), zone),
            )
        }

        then()
        assertEquals(List(3) { listOf(false, false, false) }, availability)
    }

    @Test
    fun `only scheduled appointments can be cancelled within the edit window`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val cancellable = statuses.filter {
            appointment(it).canBeCancelled(now = beforeStart.toInstant(zone), timeZone = zone)
        }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED), cancellable)
    }

    @Test
    fun `scheduled appointment can be cancelled at its edit cutoff`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val cutoff = LocalDateTime(2026, 10, 10, 10, 30)

        whenn()
        val cancellable = appointment.canBeCancelled(now = cutoff.toInstant(zone), timeZone = zone)

        then()
        assertEquals(true, cancellable)
    }

    @Test
    fun `scheduled appointment cannot be cancelled after its edit window closes`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val afterCutoff = LocalDateTime(2026, 10, 10, 10, 30, 1)

        whenn()
        val cancellable = appointment.canBeCancelled(
            now = afterCutoff.toInstant(zone),
            timeZone = zone,
        )

        then()
        assertEquals(false, cancellable)
    }

    @Test
    fun `edit window is measured in the given time zone across a daylight saving change`() =
        runUnitTest {
            given()
            val warsaw = TimeZone.of("Europe/Warsaw")
            val appointment = Appointment.stub(date = LocalDateTime(2026, 10, 24, 10, 0))
                .copy(status = AppointmentStatus.SCHEDULED)
            val naiveWallClockCutoff = LocalDateTime(2026, 10, 31, 10, 30).toInstant(warsaw)

            whenn()
            val cancellable = appointment.canBeCancelled(naiveWallClockCutoff, warsaw)

            then()
            assertEquals(false, cancellable)
        }
}
