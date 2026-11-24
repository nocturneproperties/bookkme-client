package me.bookk.feature.booking.domain.impl

import library.money.api.Money
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
                listOf(
                    SelectedService(beardTrim.id, 2, beardTrim.price),
                    SelectedService(haircut.id, 1, haircut.price),
                ),
            ),
            fixture.dataSource.selectionOf(businessId),
        )
    }

    @Test
    fun `approves each service at the price it is shown at`() = runUnitTest {
        given()
        val fixture = Fixture()
        val raised = haircut.copy(price = Money(70_000, Money.SupportedCurrency.UAH))

        whenn()
        fixture.sut(businessId, listOf(PreparedService(raised, 1, approvedPrice = haircut.price)))

        then()
        assertEquals(
            raised.price,
            fixture.dataSource.selectionOf(businessId).services.single().approvedPrice,
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
            BookingSelection(listOf(SelectedService(haircut.id, 1, haircut.price))),
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
            BookingSelection(listOf(SelectedService(beardTrim.id, 1, STUB_APPROVED_PRICE))),
            fixture.dataSource.selectionOf(otherBusinessId),
        )
    }

    @Test
    fun `forgets the specialist picked for the services saved before`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, beardTrim.id to 1, specialistId = Uuid.random())

        whenn()
        fixture.sut(businessId, listOf(PreparedService(haircut, 1)))

        then()
        assertEquals(null, fixture.dataSource.selectionOf(businessId).specialistId)
    }
}
