package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class DeclineAppointmentRequestImpl(
    private val dataSource: AppointmentRequestDataSource
) : DeclineAppointmentRequest {

    override suspend fun invoke(requestId: Uuid, businessId: Uuid, reason: String) =
        runCatching {
            dataSource.declineAppointmentRequest(requestId, businessId, reason)
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.REQUEST_ALREADY_DECLINED -> throw DeclineAppointmentRequest.Error.AlreadyDeclined()
                AppointmentErrorCodes.REQUEST_ALREADY_APPROVED -> throw DeclineAppointmentRequest.Error.AlreadyApproved()
            }
        }.getOrThrow()
}
