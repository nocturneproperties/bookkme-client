package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.employees.domain.api.UpdateEmployee
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
        val sut = UpdateEmployeeImpl(dataSource)
    }

    @Test
    fun `returns updated employee from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.updateEmployee(employee) } returns employee

        whenn()
        val result = fixture.sut(employee)

        then()
        assertEquals(employee, result)
    }

    @Test
    fun `throws ValidationError on BUSINESS_EMPLOYEE_VALIDATION_ERROR`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
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
        everySuspend { fixture.dataSource.updateEmployee(employee) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        then()
        assertFailsWith<UpdateEmployee.Error.InvalidDayOffRange> {
            fixture.sut(employee)
        }
    }
}
