package me.bookk.feature.appointments.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class WithdrawAppointmentRequestImpl(
    private val dataSource: AppointmentRequestDataSource,
) : WithdrawAppointmentRequest {

    override suspend fun invoke(requestId: Uuid) {
        runSuspendCatching { dataSource.withdrawAppointmentRequest(requestId) }
            .onFailure { throw it.toWithdrawError() }
        runSuspendCatching {
            dataSource.saveAppointmentRequestStatusInDb(
                requestId,
                AppointmentRequestStatus.CANCELLED,
            )
        }
    }

    private fun Throwable.toWithdrawError(): Throwable {
        return when {
            this is DomainError.NotFound -> Error.NotFound()
            this !is DomainError.BusinessError -> this
            errorCode == AppointmentErrorCodes.REQUEST_ALREADY_APPROVED -> Error.AlreadyApproved()
            errorCode == AppointmentErrorCodes.REQUEST_ALREADY_DECLINED -> Error.AlreadyDeclined()
            else -> this
        }
    }
}
