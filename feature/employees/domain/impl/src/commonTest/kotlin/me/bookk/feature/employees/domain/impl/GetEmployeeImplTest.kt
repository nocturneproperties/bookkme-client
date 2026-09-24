package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
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
import me.bookk.feature.employees.domain.api.GetEmployee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetEmployeeImplTest {

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
        val sut = GetEmployeeImpl(dataSource)
    }

    @Test
    fun `returns employee stored in the database`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.getEmployeeFromDb(employee.id) } returns employee

        whenn()
        val result = fixture.sut(employee.id)

        then()
        assertEquals(employee, result)
    }

    @Test
    fun `throws NotFound when the employee is not in the database`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getEmployeeFromDb(id) } returns null

        whenn()
        then()
        assertFailsWith<GetEmployee.Error.NotFound> {
            fixture.sut(id)
        }
    }
}
