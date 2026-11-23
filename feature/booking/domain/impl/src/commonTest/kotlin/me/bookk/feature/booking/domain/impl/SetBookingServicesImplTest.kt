package me.bookk.feature.booking.domain.impl

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.booking.domain.datasource.BookingSelection.SelectedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class SetBookingServicesImplTest {

    private val businessId = Uuid.random()
    private val hair = stubGroup(businessId, "Hair", createdAtMs = 1_000)
    private val haircut = stubService(
        hair,
        "Haircut",
        45.minutes,
        60_000,
    ).toExpectedBookingService()
    private val beardTrim = stubService(
        hair,
        "Beard trim",
        20.minutes,
        30_000,
    ).toExpectedBookingService()

    private class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val sut = SetBookingServicesImpl(dataSource)
    }

    @Test
    fun `saves the picked services with their counts in the given order`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(businessId, listOf(PreparedService(beardTrim, 2), PreparedService(haircut, 1)))

        then()
        assertEquals(
            BookingSelection(
                listOf(SelectedService(beardTrim.id, 2), SelectedService(haircut.id, 1)),
            ),
            fixture.dataSource.selectionOf(businessId),
        )
    }

    @Test
    fun `replaces the services saved before`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, beardTrim.id to 3)

        whenn()
        fixture.sut(businessId, listOf(PreparedService(haircut, 1)))

        then()
        assertEquals(
            BookingSelection(listOf(SelectedService(haircut.id, 1))),
            fixture.dataSource.selectionOf(businessId),
        )
    }

    @Test
    fun `leaves the services saved for other businesses alone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusinessId = Uuid.random()
        fixture.dataSource.select(otherBusinessId, beardTrim.id to 1)

        whenn()
        fixture.sut(businessId, listOf(PreparedService(haircut, 1)))

        then()
        assertEquals(
            BookingSelection(listOf(SelectedService(beardTrim.id, 1))),
            fixture.dataSource.selectionOf(otherBusinessId),
        )
    }
}
