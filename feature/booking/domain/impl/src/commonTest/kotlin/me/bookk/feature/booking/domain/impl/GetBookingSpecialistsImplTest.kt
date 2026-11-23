package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingSpecialistsImplTest {

    private val businessId = Uuid.random()
    private val hair = stubGroup(businessId, "Hair", createdAtMs = 1_000)
    private val haircut = stubService(hair, "Haircut", 45.minutes, 60_000)
    private val beardTrim = stubService(hair, "Beard trim", 20.minutes, 30_000)
    private val coloring = stubService(hair, "Coloring", 90.minutes, 150_000)
    private val andrii = stubEmployee(businessId, "Andrii", "Koval", listOf(haircut, beardTrim))
    private val maria = stubEmployee(businessId, "Maria", "Shevchuk", listOf(haircut))
    private val taras = stubEmployee(
        businessId,
        "Taras",
        "Bondar",
        listOf(coloring, beardTrim, haircut),
    )

    private inner class Fixture {
        val dataSource = FakeBookingPreparationDataSource()
        val savedEmployees = MutableStateFlow<List<BookableEmployee>>(emptyList())
        val getBookableEmployees = mock<GetBookableEmployees> {
            every { flow(businessId) } returns savedEmployees
            everySuspend { refresh(businessId) } returns listOf(andrii, maria, taras)
        }
        val sut = GetBookingSpecialistsImpl(dataSource, getBookableEmployees)

        fun saveEmployees(vararg employees: BookableEmployee) {
            savedEmployees.value = employees.toList()
        }
    }

    private fun BookableEmployee.toExpectedSpecialist(): BookingSpecialist {
        return BookingSpecialist(id = id, name = name, lastName = lastName)
    }

    @Test
    fun `returns the refreshed employees who do every picked service`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircut.id to 1, beardTrim.id to 2)

        whenn()
        val specialists = fixture.sut.refresh(businessId)

        then()
        assertEquals(
            listOf(andrii.toExpectedSpecialist(), taras.toExpectedSpecialist()),
            specialists,
        )
    }

    @Test
    fun `lists the saved employees who do every picked service in the business's order`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.dataSource.select(businessId, haircut.id to 1, beardTrim.id to 2)
            fixture.saveEmployees(andrii, maria, taras)

            whenn()
            val specialists = fixture.sut.flow(businessId).first()

            then()
            assertEquals(
                listOf(andrii.toExpectedSpecialist(), taras.toExpectedSpecialist()),
                specialists,
            )
        }

    @Test
    fun `lists nobody when no employee does every picked service`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, coloring.id to 1, haircut.id to 1)
        fixture.saveEmployees(andrii, maria)

        whenn()
        val specialists = fixture.sut.flow(businessId).first()

        then()
        assertTrue(specialists.isEmpty())
    }

    @Test
    fun `lists nobody while no employees are saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircut.id to 1)

        whenn()
        val specialists = fixture.sut.flow(businessId).first()

        then()
        assertTrue(specialists.isEmpty())
    }

    @Test
    fun `follows the services picked later`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircut.id to 1)
        fixture.saveEmployees(andrii, maria, taras)

        whenn()
        fixture.dataSource.select(businessId, coloring.id to 1)

        then()
        assertEquals(listOf(taras.toExpectedSpecialist()), fixture.sut.flow(businessId).first())
    }

    @Test
    fun `filters by the services picked for this business only`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircut.id to 1)
        fixture.dataSource.select(Uuid.random(), coloring.id to 1)
        fixture.saveEmployees(andrii, maria, taras)

        whenn()
        val specialists = fixture.sut.flow(businessId).first()

        then()
        assertEquals(
            listOf(andrii, maria, taras).map { it.toExpectedSpecialist() },
            specialists,
        )
    }
}
