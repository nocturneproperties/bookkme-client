package me.bookk.feature.booking.domain.impl

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
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.uuid.Uuid

class WithdrawBookingRequestImplTest {

    private val bookingId = Uuid.random()

    private class Fixture {
        val withdrawAppointmentRequest = mock<WithdrawAppointmentRequest> {
            everySuspend { invoke(any()) } returns Unit
        }
        val getUpcomingBookings = mock<GetUpcomingBookings> {
            everySuspend { refresh() } returns emptyList()
        }
        val sut = WithdrawBookingRequestImpl(withdrawAppointmentRequest, getUpcomingBookings)

        fun failWithdrawing(error: Throwable) {
            everySuspend { withdrawAppointmentRequest(any()) } throws error
        }
    }

    @Test
    fun `withdraws the request of the booking`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(bookingId)

        then()
        verifySuspend { fixture.withdrawAppointmentRequest(bookingId) }
    }

    @Test
    fun `leaves the upcoming bookings as they are once the request is withdrawn`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(bookingId)

        then()
        verifySuspend(VerifyMode.not) { fixture.getUpcomingBookings.refresh() }
    }

    @Test
    fun `throws AlreadyConfirmed when the business approved the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawAppointmentRequest.Error.AlreadyApproved())

        whenn()
        then()
        assertFailsWith<WithdrawBookingRequest.Error.AlreadyConfirmed> { fixture.sut(bookingId) }
    }

    @Test
    fun `throws NoLongerPending when the business declined the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawAppointmentRequest.Error.AlreadyDeclined())

        whenn()
        then()
        assertFailsWith<WithdrawBookingRequest.Error.NoLongerPending> { fixture.sut(bookingId) }
    }

    @Test
    fun `throws NoLongerPending when the request no longer exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawAppointmentRequest.Error.NotFound())

        whenn()
        then()
        assertFailsWith<WithdrawBookingRequest.Error.NoLongerPending> { fixture.sut(bookingId) }
    }

    @Test
    fun `refreshes the upcoming bookings when the request was already answered`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawAppointmentRequest.Error.AlreadyApproved())

        whenn()
        runCatching { fixture.sut(bookingId) }

        then()
        verifySuspend { fixture.getUpcomingBookings.refresh() }
    }

    @Test
    fun `keeps the answered error when the upcoming bookings cannot be refreshed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawAppointmentRequest.Error.AlreadyDeclined())
        everySuspend { fixture.getUpcomingBookings.refresh() } throws IllegalStateException()

        whenn()
        then()
        assertFailsWith<WithdrawBookingRequest.Error.NoLongerPending> {
            fixture.sut(bookingId)
        }
    }

    @Test
    fun `rethrows any other failure without refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(IllegalStateException())

        whenn()
        val error = runCatching { fixture.sut(bookingId) }.exceptionOrNull()

        then()
        assertIs<IllegalStateException>(error)
        verifySuspend(VerifyMode.not) { fixture.getUpcomingBookings.refresh() }
    }
}
