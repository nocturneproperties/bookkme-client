package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class CommonBookableEmployeeCacheTest {

    private val clients = mutableListOf<HttpClient>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        clients.forEach { it.close() }
        Dispatchers.resetMain()
    }

    private val businessId = Uuid.random()
    private val hair =
        ServiceGroup(Uuid.random(), businessId, "Hair", Instant.fromEpochMilliseconds(1_000))
    private val haircut = service("Haircut", 45, 60_000)
    private val beardTrim = service("Beard trim", 20, 30_000)
    private val andrii = employee("Andrii", listOf(haircut, beardTrim), weekdaySchedule())
    private val maria = employee("Maria", listOf(haircut), WorkingSchedule())

    private inner class Fixture {
        val client = HttpClient(MockEngine { error("Cache operations must not use the network") })
            .also { clients.add(it) }
        val dao = FakeBookableEmployeeDao()
        val sut = CommonBookableEmployeeDataSource(client, dao)
    }

    private fun service(name: String, minutes: Int, priceCents: Long): Service {
        return Service(
            id = Uuid.random(),
            businessId = businessId,
            group = hair,
            name = name,
            duration = minutes.minutes,
            price = Money(priceCents, Money.SupportedCurrency.UAH),
            isAvailable = true,
            createdAt = Instant.fromEpochMilliseconds(2_000),
        )
    }

    private fun employee(
        name: String,
        services: List<Service>,
        schedule: WorkingSchedule,
        businessId: Uuid = this.businessId,
    ): BookableEmployee {
        return BookableEmployee(
            id = Uuid.random(),
            businessId = businessId,
            name = name,
            lastName = "Koval",
            services = services,
            schedule = schedule,
        )
    }

    private fun weekdaySchedule(): WorkingSchedule {
        return WorkingSchedule(
            days = mapOf(
                DayOfWeek.MONDAY to DayOfWeekSchedule(
                    dayOfWeek = DayOfWeek.MONDAY,
                    workingTime = listOf(
                        WorkHour(LocalTime(9, 0), LocalTime(13, 0)),
                        WorkHour(LocalTime(14, 0), LocalTime(18, 0)),
                    ),
                    isActive = true,
                ),
                DayOfWeek.SUNDAY to DayOfWeekSchedule(
                    dayOfWeek = DayOfWeek.SUNDAY,
                    workingTime = emptyList(),
                    isActive = false,
                ),
            ),
            dayOffs = listOf(DayOffRange(LocalDate(2026, 12, 24), LocalDate(2026, 12, 26))),
        )
    }

    @Test
    fun `has nobody saved for a business initially`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val saved = fixture.sut.observeBookableEmployeesInDb(businessId).first()

        then()
        assertTrue(saved.isEmpty())
    }

    @Test
    fun `restores the saved employees with their services and schedule`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(andrii, maria))
        val recreated = CommonBookableEmployeeDataSource(fixture.client, fixture.dao)

        whenn()
        val saved = recreated.observeBookableEmployeesInDb(businessId).first()

        then()
        assertEquals(listOf(andrii, maria), saved)
    }

    @Test
    fun `keeps the order the employees were fetched in`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(maria, andrii))

        then()
        assertEquals(
            listOf(maria, andrii),
            fixture.sut.observeBookableEmployeesInDb(businessId).first(),
        )
    }

    @Test
    fun `replaces the employees saved before for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(andrii, maria))
        val stillBookable = maria.copy(services = listOf(haircut, beardTrim))

        whenn()
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(stillBookable))

        then()
        assertEquals(
            listOf(stillBookable),
            fixture.sut.observeBookableEmployeesInDb(businessId).first(),
        )
    }

    @Test
    fun `keeps the employees of each business apart`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusinessId = Uuid.random()
        val taras = employee("Taras", emptyList(), WorkingSchedule(), otherBusinessId)
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(andrii))

        whenn()
        fixture.sut.replaceBookableEmployeesInDb(otherBusinessId, listOf(taras))

        then()
        assertEquals(listOf(andrii), fixture.sut.observeBookableEmployeesInDb(businessId).first())
    }

    @Test
    fun `forgets every business's employees on logout`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.replaceBookableEmployeesInDb(businessId, listOf(andrii, maria))

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertTrue(fixture.sut.observeBookableEmployeesInDb(businessId).first().isEmpty())
    }
}
