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
import me.bookk.feature.employees.domain.api.ApproveEmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class ApproveEmployeeInvitationImplTest {

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
        val dataSource = mock<EmployeeInvitationDataSource>()
        val sut = ApproveEmployeeInvitationImpl(dataSource)
    }

    @Test
    fun `returns created employee from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        val employee = stubEmployee(businessId)
        everySuspend { fixture.dataSource.approveInvitation(businessId, id) } returns employee

        whenn()
        val result = fixture.sut(businessId, id)

        then()
        assertEquals(employee, result)
    }

    @Test
    fun `throws AlreadyProcessed on BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.approveInvitation(businessId, id) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED, "msg")

        whenn()
        then()
        assertFailsWith<ApproveEmployeeInvitation.Error.AlreadyProcessed> {
            fixture.sut(businessId, id)
        }
    }

    @Test
    fun `throws EmployeeExists on BUSINESS_EMPLOYEE_EXISTS`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.approveInvitation(businessId, id) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<ApproveEmployeeInvitation.Error.EmployeeExists> {
            fixture.sut(businessId, id)
        }
    }
}
