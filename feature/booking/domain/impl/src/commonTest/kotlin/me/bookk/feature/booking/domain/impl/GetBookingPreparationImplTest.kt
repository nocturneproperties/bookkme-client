package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingPreparationImplTest {

    private val businessId = Uuid.random()
    private val hair = stubGroup(businessId, "Hair", createdAtMs = 1_000)
    private val haircut = stubService(hair, "Haircut", 45.minutes, 60_000)
    private val beardTrim = stubService(hair, "Beard trim", 20.minutes, 30_000)

    private inner class Fixture {
        val services = MutableStateFlow(listOf(haircut, beardTrim))
        val dataSource = FakeBookingPreparationDataSource()
        val getBusinessServices = mock<GetBusinessServices> {
            every { flow(any()) } returns services
        }
        val sut = GetBookingPreparationImpl(dataSource, getBusinessServices)
    }

    private fun prepared(service: Service, quantity: Int): PreparedService {
        return PreparedService(service.toExpectedBookingService(), quantity)
    }

    @Test
    fun `has nothing prepared for a business nothing was picked for`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val preparation = fixture.sut.flow(businessId).first()

        then()
        assertEquals(BookingPreparation(emptyList()), preparation)
    }

    @Test
    fun `lists the picked services with their counts in the order they were picked`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.dataSource.select(businessId, beardTrim.id to 2, haircut.id to 1)

            whenn()
            val preparation = fixture.sut.flow(businessId).first()

            then()
            assertEquals(
                BookingPreparation(listOf(prepared(beardTrim, 2), prepared(haircut, 1))),
                preparation,
            )
        }

    @Test
    fun `describes a picked service as the business currently offers it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircut.id to 1)
        val repriced = haircut.copy(price = Money(70_000, Money.SupportedCurrency.UAH))

        whenn()
        fixture.services.value = listOf(repriced)
        val preparation = fixture.sut.flow(businessId).first()

        then()
        assertEquals(BookingPreparation(listOf(prepared(repriced, 1))), preparation)
    }

    @Test
    fun `leaves out a picked service the business no longer offers`() = runUnitTest {
        given()
        val fixture = Fixture()
        val removed = stubService(hair, "Removed", 30.minutes, 10_000)
        fixture.dataSource.select(businessId, haircut.id to 1, beardTrim.id to 1, removed.id to 1)

        whenn()
        fixture.services.value = listOf(haircut, beardTrim.copy(isAvailable = false))
        val preparation = fixture.sut.flow(businessId).first()

        then()
        assertEquals(BookingPreparation(listOf(prepared(haircut, 1))), preparation)
    }

    @Test
    fun `keeps the picks of each business apart`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusinessId = Uuid.random()
        fixture.dataSource.select(otherBusinessId, haircut.id to 3)

        whenn()
        val preparation = fixture.sut.flow(businessId).first()

        then()
        assertEquals(BookingPreparation(emptyList()), preparation)
    }

    @Test
    fun `carries the specialist picked for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val specialistId = Uuid.random()
        fixture.dataSource.select(businessId, haircut.id to 1, specialistId = specialistId)

        whenn()
        val preparation = fixture.sut.flow(businessId).first()

        then()
        assertEquals(
            BookingPreparation(listOf(prepared(haircut, 1)), specialistId),
            preparation,
        )
    }
}
