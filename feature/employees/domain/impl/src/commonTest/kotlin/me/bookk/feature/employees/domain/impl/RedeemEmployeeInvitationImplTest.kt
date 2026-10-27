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
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation
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
class RedeemEmployeeInvitationImplTest {

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
        val refreshBusinessInfo = mock<RefreshBusinessInfo>()
        val switchDashboardBusiness = mock<SwitchDashboardBusiness>()
        val sut = RedeemEmployeeInvitationImpl(dataSource, refreshBusinessInfo, switchDashboardBusiness)
    }

    @Test
    fun `returns created employee from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        val employee = stubEmployee()
        everySuspend { fixture.dataSource.redeemInvitation(code) } returns employee
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        val result = fixture.sut(code)

        then()
        assertEquals(employee, result)
    }

    @Test
    fun `refreshes business info after a successful redeem`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.dataSource.redeemInvitation(code) } returns stubEmployee()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut(code)

        then()
        verifySuspend { fixture.refreshBusinessInfo() }
    }

    @Test
    fun `switches dashboard business to the joined business after a successful redeem`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.redeemInvitation(code) } returns stubEmployee(businessId = businessId)
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut(code)

        then()
        verifySuspend { fixture.switchDashboardBusiness(businessId) }
    }

    @Test
    fun `does not refresh or switch business when redeem fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.dataSource.redeemInvitation(code) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<RedeemEmployeeInvitation.Error.EmployeeExists> {
            fixture.sut(code)
        }
        verifySuspend(VerifyMode.exactly(0)) { fixture.refreshBusinessInfo() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.switchDashboardBusiness(any()) }
    }

    @Test
    fun `throws AlreadyProcessed on BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.dataSource.redeemInvitation(code) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED, "msg")

        whenn()
        then()
        assertFailsWith<RedeemEmployeeInvitation.Error.AlreadyProcessed> {
            fixture.sut(code)
        }
    }

    @Test
    fun `throws EmployeeExists on BUSINESS_EMPLOYEE_EXISTS`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.dataSource.redeemInvitation(code) } throws
            DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<RedeemEmployeeInvitation.Error.EmployeeExists> {
            fixture.sut(code)
        }
    }
}
