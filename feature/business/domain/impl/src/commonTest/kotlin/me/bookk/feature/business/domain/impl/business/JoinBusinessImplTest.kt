package me.bookk.feature.business.domain.impl.business

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
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

private fun stubEmployee() = Employee(
    id = Uuid.random(),
    businessId = Uuid.random(),
    name = "Jane",
    lastName = "Doe",
    phone = "123456789",
    email = "jane@example.com",
    userId = Uuid.random(),
    services = emptyList(),
    schedule = WorkingSchedule(),
    createdAt = Instant.fromEpochMilliseconds(0)
)

@OptIn(ExperimentalCoroutinesApi::class)
class JoinBusinessImplTest {

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
        val redeemEmployeeInvitation = mock<RedeemEmployeeInvitation>()
        val sut = JoinBusinessImpl(redeemEmployeeInvitation)
    }

    @Test
    fun `redeems the invitation code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.redeemEmployeeInvitation(code) } returns stubEmployee()

        whenn()
        fixture.sut(code)

        then()
        verifySuspend { fixture.redeemEmployeeInvitation(code) }
    }

    @Test
    fun `throws AlreadyProcessed when the invitation was already processed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.redeemEmployeeInvitation(code) } throws
            RedeemEmployeeInvitation.Error.AlreadyProcessed(RuntimeException())

        whenn()
        then()
        assertFailsWith<JoinBusiness.Error.AlreadyProcessed> {
            fixture.sut(code)
        }
    }

    @Test
    fun `throws EmployeeExists when the user is already an employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        val code = "ABCD1234"
        everySuspend { fixture.redeemEmployeeInvitation(code) } throws
            RedeemEmployeeInvitation.Error.EmployeeExists(RuntimeException())

        whenn()
        then()
        assertFailsWith<JoinBusiness.Error.EmployeeExists> {
            fixture.sut(code)
        }
    }
}
