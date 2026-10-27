package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource

internal class CreateAppointmentRequestImpl(
    private val appointmentRequestDataSource: AppointmentRequestDataSource
) : CreateAppointmentRequest {

    override suspend fun invoke(draft: AppointmentRequestDraft) {
        val normalizedDraft = draft.copy(services = draft.services.mergedByService())
        runCatching {
            appointmentRequestDataSource.createAppointmentRequest(normalizedDraft)
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.REQUEST_EXISTS -> throw Error.RequestForThisTimeExists()
                AppointmentErrorCodes.TIME_NOT_ALLOWED -> throw Error.TimeIsNotAllowed()
                AppointmentErrorCodes.DATE_NOT_ALLOWED -> throw Error.DateIsNotAllowed()
                AppointmentErrorCodes.DATE_IN_PAST -> throw Error.DateInPast()
                AppointmentErrorCodes.PRICE_CHANGED -> throw Error.PriceChanged()
                AppointmentErrorCodes.DURATION_CHANGED -> throw Error.DurationChanged()
                AppointmentErrorCodes.SERVICES_VALIDATION_FAILED -> throw Error.ServicesDoNotMatchOffer()
                AppointmentErrorCodes.QUOTE_TOKEN_ALREADY_USED -> throw Error.OfferAlreadyUsed()
                AppointmentErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND -> throw Error.ServiceNotFound()
                AppointmentErrorCodes.BUSINESS_EMPLOYEE_NOT_EXISTS -> throw Error.EmployeeNotFound()
                AppointmentErrorCodes.BUSINESS_EMPLOYEE_SUSPENDED -> throw Error.EmployeeSuspended()
            }
        }.getOrThrow()
    }

    private fun List<RequestedService>.mergedByService(): List<RequestedService> {
        return groupBy { it.serviceId }.map { (serviceId, entries) ->
            RequestedService(serviceId = serviceId, count = entries.sumOf { it.count })
        }
    }
}
