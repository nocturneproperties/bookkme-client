package me.bookk.feature.booking.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest.Error
import kotlin.uuid.Uuid

internal class WithdrawBookingRequestImpl(
    private val withdrawAppointmentRequest: WithdrawAppointmentRequest,
    private val getUpcomingBookings: GetUpcomingBookings,
) : WithdrawBookingRequest {

    override suspend fun invoke(bookingId: Uuid) {
        runSuspendCatching { withdrawAppointmentRequest(bookingId) }
            .onFailure { error -> throw error.toBookingError().also { refreshIfAnswered(it) } }
    }

    private suspend fun refreshIfAnswered(error: Throwable) {
        if (error !is Error) return
        runSuspendCatching { getUpcomingBookings.refresh() }
    }

    private fun Throwable.toBookingError(): Throwable {
        return when (this) {
            is WithdrawAppointmentRequest.Error.AlreadyApproved -> Error.AlreadyConfirmed()

            is WithdrawAppointmentRequest.Error.AlreadyDeclined,
            is WithdrawAppointmentRequest.Error.NotFound,
            -> Error.NoLongerPending()

            else -> this
        }
    }
}
