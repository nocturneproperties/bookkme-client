package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

class WithdrawAppointmentRequestImplTest {

    private val requestId = Uuid.random()

    private class Fixture {
        val dataSource = mock<AppointmentRequestDataSource> {
            everySuspend { withdrawAppointmentRequest(any()) } returns Unit
            everySuspend { saveAppointmentRequestStatusInDb(any(), any()) } returns Unit
        }
        val sut = WithdrawAppointmentRequestImpl(dataSource)
    }

    @Test
    fun `withdraws the request on the backend`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(requestId)

        then()
        verifySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) }
    }

    @Test
    fun `marks the withdrawn request cancelled in the cache`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(requestId)

        then()
        verifySuspend {
            fixture.dataSource.saveAppointmentRequestStatusInDb(
                requestId,
                AppointmentRequestStatus.CANCELLED,
            )
        }
    }

    @Test
    fun `reports the request withdrawn when the cache cannot be updated`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.saveAppointmentRequestStatusInDb(any(), any()) } throws
            IllegalStateException()

        whenn()
        val result = runCatching { fixture.sut(requestId) }

        then()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `keeps the cached request when withdrawing fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) } throws
            IllegalStateException()

        whenn()
        runCatching { fixture.sut(requestId) }

        then()
        verifySuspend(VerifyMode.not) {
            fixture.dataSource.saveAppointmentRequestStatusInDb(any(), any())
        }
    }

    @Test
    fun `throws AlreadyApproved when the business approved the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.REQUEST_ALREADY_APPROVED, "msg")

        whenn()
        then()
        assertFailsWith<WithdrawAppointmentRequest.Error.AlreadyApproved> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `throws AlreadyDeclined when the request is no longer pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.REQUEST_ALREADY_DECLINED, "msg")

        whenn()
        then()
        assertFailsWith<WithdrawAppointmentRequest.Error.AlreadyDeclined> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `throws NotFound when the backend does not know the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) } throws
            DomainError.NotFound(IllegalStateException())

        whenn()
        then()
        assertFailsWith<WithdrawAppointmentRequest.Error.NotFound> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `rethrows any other failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.withdrawAppointmentRequest(requestId) } throws
            DomainError.NoConnectionError(IllegalStateException())

        whenn()
        then()
        assertFailsWith<DomainError.NoConnectionError> {
            fixture.sut(requestId)
        }
    }
}
