package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.datetime.LocalDate
import me.bookk.core.domain.entity.Error.BusinessError
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetFreeSlots.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.FreeSlot
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetFreeSlotsImplTest {

    private val query = FreeSlotsQuery(
        businessId = Uuid.random(),
        employeeId = Uuid.random(),
        services = listOf(RequestedService(Uuid.random(), 2)),
        from = LocalDate(2026, 11, 23),
        to = LocalDate(2026, 11, 29),
    )

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val sut = GetFreeSlotsImpl(dataSource)
    }

    @Test
    fun `returns the free slots of every day in the range`() = runUnitTest {
        given()
        val fixture = Fixture()
        val days = listOf(
            FreeSlotsDay(
                date = LocalDate(2026, 11, 23),
                slots = listOf(
                    FreeSlot(
                        start = Instant.parse("2026-11-23T07:00:00Z"),
                        end = Instant.parse("2026-11-23T08:00:00Z"),
                    ),
                ),
            ),
            FreeSlotsDay(date = LocalDate(2026, 11, 24), slots = emptyList()),
        )
        everySuspend { fixture.dataSource.getFreeSlots(query) } returns days

        whenn()
        val result = fixture.sut(query)

        then()
        assertEquals(days, result)
    }

    @Test
    fun `throws appointments not enabled for a business that takes no appointments`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.dataSource.getFreeSlots(query) } throws
                BusinessError(AppointmentErrorCodes.APPOINTMENTS_NOT_ENABLED, "")

            whenn()
            val call = suspend { fixture.sut(query) }

            then()
            assertFailsWith<Error.AppointmentsNotEnabled> { call() }
        }

    @Test
    fun `throws invalid service selection when the backend rejects the services`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getFreeSlots(query) } throws
            BusinessError(AppointmentErrorCodes.SERVICE_SELECTION_INVALID, "")

        whenn()
        val call = suspend { fixture.sut(query) }

        then()
        assertFailsWith<Error.InvalidServiceSelection> { call() }
    }

    @Test
    fun `throws invalid range when the backend rejects the dates`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getFreeSlots(query) } throws
            BusinessError(AppointmentErrorCodes.SLOT_RANGE_INVALID, "")

        whenn()
        val call = suspend { fixture.sut(query) }

        then()
        assertFailsWith<Error.InvalidRange> { call() }
    }
}
