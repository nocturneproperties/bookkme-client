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

class SetBookingSpecialistImplTest {

    private val businessId = Uuid.random()
    private val haircutId = Uuid.random()
    private val specialistId = Uuid.random()

    private class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val sut = SetBookingSpecialistImpl(dataSource)
    }

    @Test
    fun `saves the specialist beside the picked services`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 2)

        whenn()
        fixture.sut(businessId, specialistId)

        then()
        assertEquals(
            BookingSelection(
                listOf(SelectedService(haircutId, 2, STUB_APPROVED_PRICE)),
                specialistId,
            ),
            fixture.dataSource.selectionOf(businessId),
        )
    }

    @Test
    fun `replaces the specialist saved before`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = Uuid.random())

        whenn()
        fixture.sut(businessId, specialistId)

        then()
        assertEquals(specialistId, fixture.dataSource.selectionOf(businessId).specialistId)
    }

    @Test
    fun `leaves the specialist saved for other businesses alone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusinessId = Uuid.random()
        val otherSpecialistId = Uuid.random()
        fixture.dataSource.select(otherBusinessId, haircutId to 1, specialistId = otherSpecialistId)

        whenn()
        fixture.sut(businessId, specialistId)

        then()
        assertEquals(
            otherSpecialistId,
            fixture.dataSource.selectionOf(otherBusinessId).specialistId,
        )
    }

    @Test
    fun `forgets the slot picked for another specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        val slotStart = Instant.parse("2026-11-24T12:30:00Z")
        fixture.dataSource.select(
            businessId,
            haircutId to 1,
            specialistId = Uuid.random(),
            slotStart = slotStart,
        )

        whenn()
        fixture.sut(businessId, specialistId)

        then()
        assertEquals(null, fixture.dataSource.selectionOf(businessId).slotStart)
    }

    @Test
    fun `keeps the slot when the same specialist is picked again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val slotStart = Instant.parse("2026-11-24T12:30:00Z")
        fixture.dataSource.select(
            businessId,
            haircutId to 1,
            specialistId = specialistId,
            slotStart = slotStart,
        )

        whenn()
        fixture.sut(businessId, specialistId)

        then()
        assertEquals(slotStart, fixture.dataSource.selectionOf(businessId).slotStart)
    }
}
