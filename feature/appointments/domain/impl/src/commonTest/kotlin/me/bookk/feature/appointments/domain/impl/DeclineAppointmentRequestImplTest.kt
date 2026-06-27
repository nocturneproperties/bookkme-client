package me.bookk.feature.appointments.domain.impl

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
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class DeclineAppointmentRequestImplTest {

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
        val dataSource = mock<AppointmentRequestDataSource>()
        val sut = DeclineAppointmentRequestImpl(dataSource)
    }

    @Test
    fun `calls datasource with correct parameters`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        val businessId = Uuid.random()
        val reason = "Not available"
        everySuspend { fixture.dataSource.declineAppointmentRequest(requestId, businessId, reason) } returns Unit

        whenn()
        fixture.sut(requestId, businessId, reason)

        then()
        verifySuspend { fixture.dataSource.declineAppointmentRequest(requestId, businessId, reason) }
    }

    @Test
    fun `throws AlreadyDeclined on REQUEST_ALREADY_DECLINED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.declineAppointmentRequest(requestId, businessId, "reason") } throws
            DomainError.BusinessError(AppointmentErrorCodes.REQUEST_ALREADY_DECLINED, "msg")

        whenn()
        then()
        assertFailsWith<DeclineAppointmentRequest.Error.AlreadyDeclined> {
            fixture.sut(requestId, businessId, "reason")
        }
    }

    @Test
    fun `throws AlreadyApproved on REQUEST_ALREADY_APPROVED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.declineAppointmentRequest(requestId, businessId, "reason") } throws
            DomainError.BusinessError(AppointmentErrorCodes.REQUEST_ALREADY_APPROVED, "msg")

        whenn()
        then()
        assertFailsWith<DeclineAppointmentRequest.Error.AlreadyApproved> {
            fixture.sut(requestId, businessId, "reason")
        }
    }
}
