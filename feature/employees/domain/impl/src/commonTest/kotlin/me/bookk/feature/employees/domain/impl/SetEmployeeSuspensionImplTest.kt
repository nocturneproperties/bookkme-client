package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import me.bookk.feature.employees.domain.api.SetEmployeeSuspension
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class SetEmployeeSuspensionImplTest {

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
        val sut = SetEmployeeSuspensionImpl(dataSource)
    }

    @Test
    fun `returns the suspended employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        val suspended = stubEmployee().copy(suspendedAt = Instant.fromEpochMilliseconds(1))
        everySuspend { fixture.dataSource.setEmployeeSuspension(suspended.businessId, suspended.id, true) } returns suspended
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        val result = fixture.sut(suspended.businessId, suspended.id, true)

        then()
        assertEquals(suspended, result)
    }

    @Test
    fun `saves the returned employee in the database`() = runUnitTest {
        given()
        val fixture = Fixture()
        val reinstated = stubEmployee()
        everySuspend { fixture.dataSource.setEmployeeSuspension(reinstated.businessId, reinstated.id, false) } returns reinstated
        everySuspend { fixture.dataSource.saveEmployeesInDb(any()) } returns Unit

        whenn()
        fixture.sut(reinstated.businessId, reinstated.id, false)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveEmployeesInDb(listOf(reinstated)) }
    }

    @Test
    fun `throws owner suspension not allowed when suspending the business owner`() = runUnitTest {
        given()
        val fixture = Fixture()
        val owner = stubEmployee()
        everySuspend { fixture.dataSource.setEmployeeSuspension(any(), any(), any()) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_OWNER_SUSPENSION_NOT_ALLOWED, "owner")

        whenn()
        then()
        assertFailsWith<SetEmployeeSuspension.Error.OwnerSuspensionNotAllowed> {
            fixture.sut(owner.businessId, owner.id, true)
        }
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveEmployeesInDb(any()) }
    }

    @Test
    fun `rethrows other business errors unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.setEmployeeSuspension(any(), any(), any()) } throws
            DomainError.BusinessError(errorCode = 1, message = "other")

        whenn()
        then()
        assertFailsWith<DomainError.BusinessError> {
            fixture.sut(employee.businessId, employee.id, true)
        }
    }

    @Test
    fun `rethrows business access suspended unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.setEmployeeSuspension(any(), any(), any()) } throws
            DomainError.BusinessAccessSuspended("suspended")

        whenn()
        then()
        assertFailsWith<DomainError.BusinessAccessSuspended> {
            fixture.sut(employee.businessId, employee.id, true)
        }
    }
}
