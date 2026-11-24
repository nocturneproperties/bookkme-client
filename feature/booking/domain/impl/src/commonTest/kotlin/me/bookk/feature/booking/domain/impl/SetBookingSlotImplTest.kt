package me.bookk.feature.booking.domain.impl

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.booking.domain.datasource.BookingSelection.SelectedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SetBookingSlotImplTest {

    private val businessId = Uuid.random()
    private val haircutId = Uuid.random()
    private val specialistId = Uuid.random()
    private val start = Instant.parse("2026-11-24T12:30:00Z")

    private class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val sut = SetBookingSlotImpl(dataSource)
    }

    @Test
    fun `saves the slot beside the picked services and specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 2, specialistId = specialistId)

        whenn()
        fixture.sut(businessId, start)

        then()
        assertEquals(
            BookingSelection(
                listOf(SelectedService(haircutId, 2, STUB_APPROVED_PRICE)),
                specialistId,
                start,
            ),
            fixture.dataSource.selectionOf(businessId),
        )
    }

    @Test
    fun `leaves the slot saved for other businesses alone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusinessId = Uuid.random()
        val otherStart = Instant.parse("2026-11-25T08:00:00Z")
        fixture.dataSource.select(otherBusinessId, haircutId to 1, slotStart = otherStart)

        whenn()
        fixture.sut(businessId, start)

        then()
        assertEquals(otherStart, fixture.dataSource.selectionOf(otherBusinessId).slotStart)
    }
}
