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
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
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
class CreateEmployeeInvitationImplTest {

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
        val sut = CreateEmployeeInvitationImpl(dataSource)
    }

    @Test
    fun `returns created invitation from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitation = stubEmployeeInvitation(businessId)
        everySuspend { fixture.dataSource.createInvitation(businessId, "jane@example.com") } returns invitation

        whenn()
        val result = fixture.sut(businessId, "jane@example.com")

        then()
        assertEquals(invitation, result)
    }

    @Test
    fun `throws InvitationExists on BUSINESS_EMPLOYEE_INVITATION_EXISTS`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.createInvitation(businessId, "jane@example.com") } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<CreateEmployeeInvitation.Error.InvitationExists> {
            fixture.sut(businessId, "jane@example.com")
        }
    }

    @Test
    fun `throws ValidationError on BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.createInvitation(businessId, "bad-email") } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR, "msg")

        whenn()
        then()
        assertFailsWith<CreateEmployeeInvitation.Error.ValidationError> {
            fixture.sut(businessId, "bad-email")
        }
    }

    @Test
    fun `throws EmployeeExists on BUSINESS_EMPLOYEE_EXISTS`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.createInvitation(businessId, "jane@example.com") } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<CreateEmployeeInvitation.Error.EmployeeExists> {
            fixture.sut(businessId, "jane@example.com")
        }
    }
}
