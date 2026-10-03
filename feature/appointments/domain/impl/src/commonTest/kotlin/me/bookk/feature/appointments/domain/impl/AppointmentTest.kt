package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.LocalDateTime
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class AppointmentTest {

    private val start = LocalDateTime(2026, 10, 3, 10, 0)
    private val beforeStart = LocalDateTime(2026, 10, 3, 9, 59)

    private fun appointment(status: AppointmentStatus) = Appointment.stub(date = start).copy(status = status)

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
}
