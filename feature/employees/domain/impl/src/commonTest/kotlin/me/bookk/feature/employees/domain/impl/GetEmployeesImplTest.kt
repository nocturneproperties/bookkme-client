package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetEmployeesImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<EmployeeDataSource>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetEmployeesImpl(dataSource, observeDashboardBusinessChanges)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission()
        )
    )

    @Test
    fun `flow emits empty list when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `flow emits the db employees for the current dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val employees = listOf(stubEmployee(business.id))
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeEmployeesDBChanges(business.id) } returns flowOf(employees)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(employees, result)
    }

    @Test
    fun `flow re-resolves the db observation when the dashboard business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1)
        val firstBusiness = stubBusiness()
        val secondBusiness = stubBusiness()
        val firstEmployees = listOf(stubEmployee(firstBusiness.id))
        val secondEmployees = listOf(stubEmployee(secondBusiness.id))
        businesses.tryEmit(firstBusiness)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.dataSource.observeEmployeesDBChanges(firstBusiness.id) } returns flowOf(firstEmployees)
        every { fixture.dataSource.observeEmployeesDBChanges(secondBusiness.id) } returns flowOf(secondEmployees)
        val results = mutableListOf<List<Employee>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow().collect { results.add(it) }
        }

        whenn()
        businesses.emit(secondBusiness)

        then()
        job.cancel()
        assertEquals(firstEmployees, results.first())
        assertEquals(secondEmployees, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeEmployeesDBChanges(business.id) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getEmployees(any()) }
    }

    @Test
    fun `refresh fetches employees from remote and saves them`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employees = listOf(stubEmployee(businessId))
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns employees
        everySuspend { fixture.dataSource.getEmployeeIdsInDb(businessId) } returns employees.map { it.id }
        everySuspend { fixture.dataSource.saveEmployeesInDb(employees) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(employees, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveEmployeesInDb(employees) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `refresh deletes local employees that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubEmployee(businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns remote
        everySuspend { fixture.dataSource.getEmployeeIdsInDb(businessId) } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.dataSource.deleteEmployeesInDb(listOf(staleId)) } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteEmployeesInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employee = stubEmployee(businessId)
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns listOf(employee)
        everySuspend { fixture.dataSource.getEmployeeIdsInDb(businessId) } returns listOf(employee.id)
        everySuspend { fixture.dataSource.saveEmployeesInDb(listOf(employee)) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteEmployeesInDb(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getEmployees(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getEmployeeIdsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteEmployeesInDb(any()) }
    }
}
