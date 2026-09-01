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
import me.bookk.feature.employees.domain.api.GetPendingInvitationsForEmail
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class GetPendingInvitationsForEmailImplTest {

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
        val sut = GetPendingInvitationsForEmailImpl(dataSource)
    }

    @Test
    fun `returns pending invitations from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val invitations = listOf(stubEmployeeInvitation(), stubEmployeeInvitation())
        everySuspend { fixture.dataSource.getPendingInvitationsForEmail("jane@example.com") } returns invitations

        whenn()
        val result = fixture.sut("jane@example.com")

        then()
        assertEquals(invitations, result)
    }

    @Test
    fun `throws ValidationError on BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getPendingInvitationsForEmail("bad-email") } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR, "msg")

        whenn()
        then()
        assertFailsWith<GetPendingInvitationsForEmail.Error.ValidationError> {
            fixture.sut("bad-email")
        }
    }
}
