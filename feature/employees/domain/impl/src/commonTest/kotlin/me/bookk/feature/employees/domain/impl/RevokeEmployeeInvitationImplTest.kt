package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class RevokeEmployeeInvitationImplTest {

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
        val sut = RevokeEmployeeInvitationImpl(dataSource)
    }

    @Test
    fun `revokes invitation through datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.revokeInvitation(businessId, id) } returns Unit

        whenn()
        fixture.sut(businessId, id)

        then()
        verifySuspend { fixture.dataSource.revokeInvitation(businessId, id) }
    }

    @Test
    fun `throws AlreadyProcessed on BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.revokeInvitation(businessId, id) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED, "msg")

        whenn()
        then()
        assertFailsWith<RevokeEmployeeInvitation.Error.AlreadyProcessed> {
            fixture.sut(businessId, id)
        }
    }
}
