package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
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
        val sut = GetEmployeesImpl(dataSource)
    }

    @Test
    fun `returns employees from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employees = listOf(stubEmployee(businessId), stubEmployee(businessId))
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns employees
        everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(employees) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(employees, result)
    }

    @Test
    fun `returns empty list when business has no employees`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns emptyList()
        everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(emptyList()) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `deletes old employees then saves new ones and marks synced`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employees = listOf(stubEmployee(businessId))
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns employees
        everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(employees) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteEmployeesInDb() }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveEmployeesInDb(employees) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when business synced before`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubEmployee(businessId))
        val remote = listOf(stubEmployee(businessId), stubEmployee(businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns Instant.fromEpochMilliseconds(0)
        everySuspend { fixture.dataSource.getEmployeesFromDb(businessId) } returns cached
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns remote
        everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
        val received = mutableListOf<List<Employee>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Employee>>>(listOf(cached, remote), received)
    }

    @Test
    fun `cached skips DB callback when business never synced before, even if DB has stale rows`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubEmployee(businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns null
        everySuspend { fixture.dataSource.getEmployees(businessId) } returns remote
        everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
        everySuspend { fixture.dataSource.saveEmployeesInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
        val received = mutableListOf<List<Employee>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Employee>>>(listOf(remote), received)
    }

    @Test
    fun `cached calls onResultAvailable with empty DB list when synced before and business has no employees`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns Instant.fromEpochMilliseconds(0)
            everySuspend { fixture.dataSource.getEmployeesFromDb(businessId) } returns emptyList()
            everySuspend { fixture.dataSource.getEmployees(businessId) } returns emptyList()
            everySuspend { fixture.dataSource.deleteEmployeesInDb() } returns Unit
            everySuspend { fixture.dataSource.saveEmployeesInDb(emptyList()) } returns Unit
            everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
            val received = mutableListOf<List<Employee>>()

            whenn()
            fixture.sut.cached(businessId) { received.add(it) }

            then()
            assertEquals<List<List<Employee>>>(listOf(emptyList(), emptyList()), received)
        }
}
