package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.LocalDateTime
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

    private val start = LocalDateTime(2026, 10, 3, 10, 0)
    private val beforeStart = LocalDateTime(2026, 10, 3, 9, 59)

    private fun appointment(status: AppointmentStatus) =
        Appointment.stub(date = start).copy(status = status)

    @Test
    fun `only a started scheduled appointment can be completed`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val completable = statuses.filter { appointment(it).canBeCompleted(now = start) }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED), completable)
    }

    @Test
    fun `scheduled appointment cannot be completed before it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val completable = appointment.canBeCompleted(now = beforeStart)

        then()
        assertEquals(false, completable)
    }

    @Test
    fun `started scheduled or completed appointment can be marked as no-show`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val markable = statuses.filter { appointment(it).canBeMarkedNoShow(now = start) }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED), markable)
    }

    @Test
    fun `appointment cannot be marked as no-show before it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val markable = appointment.canBeMarkedNoShow(now = beforeStart)

        then()
        assertEquals(false, markable)
    }

    @Test
    fun `only a scheduled appointment that has not started can be rescheduled`() = runUnitTest {
        given()
        val statuses = AppointmentStatus.entries

        whenn()
        val reschedulable = statuses.filter { appointment(it).canBeRescheduled(now = beforeStart) }

        then()
        assertEquals(listOf(AppointmentStatus.SCHEDULED), reschedulable)
    }

    @Test
    fun `scheduled appointment cannot be rescheduled once it starts`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)

        whenn()
        val reschedulable = appointment.canBeRescheduled(now = start)

        then()
        assertEquals(false, reschedulable)
    }

    @Test
    fun `scheduled appointment can be completed until seven days after it ends`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val beforeCutoff = LocalDateTime(2026, 10, 10, 10, 29, 59)

        whenn()
        val completable = appointment.canBeCompleted(now = beforeCutoff)

        then()
        assertEquals(true, completable)
    }

    @Test
    fun `scheduled appointment can be completed exactly seven days after it ends`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val cutoff = LocalDateTime(2026, 10, 10, 10, 30)

        whenn()
        val completable = appointment.canBeCompleted(now = cutoff)

        then()
        assertEquals(true, completable)
    }

    @Test
    fun `scheduled appointment cannot be completed after its edit window closes`() = runUnitTest {
        given()
        val appointment = appointment(AppointmentStatus.SCHEDULED)
        val afterCutoff = LocalDateTime(2026, 10, 10, 10, 30, 1)

        whenn()
        val completable = appointment.canBeCompleted(now = afterCutoff)

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
        val completable = appointment.canBeCompleted(now = cutoff)

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
        val completable = appointment.canBeCompleted(now = afterCutoff)

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
                appointment(it).canBeMarkedNoShow(now = cutoff)
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
            appointment(it).canBeMarkedNoShow(now = afterCutoff)
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
        val markable = appointment.canBeMarkedNoShow(now = cutoff)

        then()
        assertEquals(true, markable)
    }
}
