package me.bookk.feature.employees.presentation.screen.list

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.presentation.FakeEmployeesStateFactory
import me.bookk.feature.employees.presentation.stubEmployee
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class EmployeeListViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val businessId = Uuid.random()
        val employees = MutableStateFlow<List<Employee>>(emptyList())
        val currentBusinessId = MutableStateFlow<Uuid?>(businessId)
        val getEmployees = mock<GetEmployees> {
            every { flow() } returns employees
            everySuspend { refresh(any()) } returns emptyList()
        }
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns currentBusinessId
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = EmployeeListViewModel(
            getEmployees = getEmployees,
            observeCurrentBusinessId = observeCurrentBusinessId,
            stateFactory = FakeEmployeesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes employees of current business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getEmployees.refresh(fixture.businessId) }
    }

    @Test
    fun `groups employees by capitalized first letter sorted by full name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.employees.value = listOf(stubEmployee("bob"), stubEmployee("Anna", "Zed"), stubEmployee("Anna", "Abe"))

        then()
        val sections = sut.uiState.employeesList.items
        assertEquals(listOf("A", "B"), sections.map { it.header })
        assertEquals(listOf("Abe", "Zed"), sections[0].items.map { it.lastName })
    }

    @Test
    fun `ignores empty cache until first load finishes`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getEmployees.refresh(any()) } returns listOf(stubEmployee())

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.employeesList.items.isEmpty())
        assertTrue(sut.uiState.employeesList.isInitialLoading)
    }

    @Test
    fun `filters employees by full name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.employees.value = listOf(stubEmployee("Anna", "Smith"), stubEmployee("Bob", "Stone"))

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("stone")

        then()
        assertEquals(listOf("Bob"), sut.uiState.employeesList.items.flatMap { it.items }.map { it.name })
    }

    @Test
    fun `restores employees when search is cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.employees.value = listOf(stubEmployee("Anna"), stubEmployee("Bob"))
        (sut.uiState.searchField as FakeTextFieldState).type("anna")

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("")

        then()
        assertEquals(2, sut.uiState.employeesList.items.flatMap { it.items }.size)
    }

    @Test
    fun `navigates to add employee for current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(listOf<EmployeeListDestinations>(EmployeeListDestinations.AddEmployee(fixture.businessId)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `navigates to edit the clicked employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.employees.value = listOf(employee)
        val section = sut.uiState.employeesList.items.single()

        whenn()
        section.onItemClick(employee)

        then()
        assertEquals(listOf<EmployeeListDestinations>(EmployeeListDestinations.EditEmployee(employee.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `reloads when business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val other = Uuid.random()
        fixture.sut()

        whenn()
        fixture.currentBusinessId.value = other

        then()
        verifySuspend { fixture.getEmployees.refresh(other) }
    }

    @Test
    fun `shows mapped error when employee observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getEmployees.flow() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<EmployeeListDestinations>(EmployeeListDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
