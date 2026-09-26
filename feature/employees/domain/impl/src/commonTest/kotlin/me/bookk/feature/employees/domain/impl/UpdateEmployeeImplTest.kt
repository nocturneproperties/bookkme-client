package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateEmployeeImplTest {

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
        val isBusinessOwner = mock<IsBusinessOwner> {
            everySuspend { invoke(any(), any()) } returns false
            everySuspend { invoke(any()) } returns true
        }
        val sut = UpdateEmployeeImpl(dataSource, isBusinessOwner)

        fun stubSuccess(employee: Employee, profile: Employee = employee, permissions: Employee = employee) {
            everySuspend { dataSource.updateEmployee(employee) } returns profile
            everySuspend {
                dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
            } returns permissions
            everySuspend { dataSource.saveEmployeesInDb(any()) } returns Unit
        }
    }

    private fun Employee.withEmployeesAccess() = copy(
        permissions = permissions.copy(employees = ResourcePermission(view = true, update = true, delete = false))
    )

    @Test
    fun `sends the edited permissions in the permissions request`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee().withEmployeesAccess()
        fixture.stubSuccess(employee)

        whenn()
        fixture.sut(employee)

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
        }
    }

    @Test
    fun `does not send the permissions request for the business owner`() = runUnitTest {
        given()
        val fixture = Fixture()
        val owner = stubEmployee()
        everySuspend { fixture.isBusinessOwner(owner.userId, owner.businessId) } returns true
        everySuspend { fixture.dataSource.updateEmployee(owner) } returns owner
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        fixture.sut(owner)

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.updateEmployeePermissions(any(), any(), any()) }
    }

    @Test
    fun `does not send the permissions request when the current user is not the business owner`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee().withEmployeesAccess()
        everySuspend { fixture.isBusinessOwner(employee.businessId) } returns false
        everySuspend { fixture.dataSource.updateEmployee(employee) } returns employee
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        fixture.sut(employee)

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.updateEmployeePermissions(any(), any(), any()) }
    }

    @Test
    fun `saves and returns the profile response for the business owner`() = runUnitTest {
        given()
        val fixture = Fixture()
        val owner = stubEmployee()
        val profileResponse = owner.copy(name = "Updated")
        everySuspend { fixture.isBusinessOwner(owner.userId, owner.businessId) } returns true
        everySuspend { fixture.dataSource.updateEmployee(owner) } returns profileResponse
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        val result = fixture.sut(owner)

        then()
        assertEquals(profileResponse, result)
        verifySuspend { fixture.dataSource.saveEmployeesInDb(listOf(profileResponse)) }
    }

    @Test
    fun `returns profile from the update response and permissions from the permissions response`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        val profileResponse = employee.copy(name = "Updated")
        val permissionsResponse = employee.withEmployeesAccess()
        fixture.stubSuccess(employee, profile = profileResponse, permissions = permissionsResponse)

        whenn()
        val result = fixture.sut(employee)

        then()
        assertEquals(profileResponse.copy(permissions = permissionsResponse.permissions), result)
    }

    @Test
    fun `sends both requests concurrently`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        val permissionsRequestStarted = CompletableDeferred<Unit>()
        everySuspend { fixture.dataSource.updateEmployee(employee) } calls {
            permissionsRequestStarted.await()
            employee
        }
        everySuspend {
            fixture.dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
        } calls {
            permissionsRequestStarted.complete(Unit)
            employee
        }
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        val result = withTimeout(1_000) { fixture.sut(employee) }

        then()
        assertEquals(employee, result)
    }

    @Test
    fun `saves the merged employee in the database`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        val profileResponse = employee.copy(lastName = "Updated")
        val permissionsResponse = employee.withEmployeesAccess()
        fixture.stubSuccess(employee, profile = profileResponse, permissions = permissionsResponse)

        whenn()
        fixture.sut(employee)

        then()
        verifySuspend {
            fixture.dataSource.saveEmployeesInDb(listOf(profileResponse.copy(permissions = permissionsResponse.permissions)))
        }
    }

    @Test
    fun `does not touch the database when a request fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.updateEmployee(employee) } returns employee
        everySuspend {
            fixture.dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
        } throws IllegalStateException("network")
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        runCatching { fixture.sut(employee) }

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveEmployeesInDb(any()) }
    }

    @Test
    fun `throws ValidationError on BUSINESS_EMPLOYEE_VALIDATION_ERROR`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubSuccess(employee)
        everySuspend { fixture.dataSource.updateEmployee(employee) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_VALIDATION_ERROR, "msg")

        whenn()
        then()
        assertFailsWith<UpdateEmployee.Error.ValidationError> {
            fixture.sut(employee)
        }
    }

    @Test
    fun `throws ActiveDayWithoutWorkHours on BUSINESS_EMPLOYEE_ACTIVE_DAY_WITHOUT_WORK_HOURS`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubSuccess(employee)
        everySuspend { fixture.dataSource.updateEmployee(employee) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_ACTIVE_DAY_WITHOUT_WORK_HOURS, "msg")

        whenn()
        then()
        assertFailsWith<UpdateEmployee.Error.ActiveDayWithoutWorkHours> {
            fixture.sut(employee)
        }
    }

    @Test
    fun `throws InvalidDayOffRange on BUSINESS_EMPLOYEE_INVALID_DAY_OFF_RANGE`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubSuccess(employee)
        everySuspend { fixture.dataSource.updateEmployee(employee) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        then()
        assertFailsWith<UpdateEmployee.Error.InvalidDayOffRange> {
            fixture.sut(employee)
        }
    }

    @Test
    fun `throws InsufficientGrant on BUSINESS_INSUFFICIENT_GRANT_PERMISSION`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubSuccess(employee)
        everySuspend {
            fixture.dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
        } throws DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_INSUFFICIENT_GRANT_PERMISSION, "msg")

        whenn()
        then()
        assertFailsWith<UpdateEmployee.Error.InsufficientGrant> {
            fixture.sut(employee)
        }
    }
}
