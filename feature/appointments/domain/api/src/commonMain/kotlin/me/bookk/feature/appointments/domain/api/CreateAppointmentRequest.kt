package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft

interface CreateAppointmentRequest {
    suspend operator fun invoke(draft: AppointmentRequestDraft)

    sealed interface Error {
        class RequestForThisTimeExists : Throwable(), Error
        class TimeIsNotAllowed : Throwable(), Error
        class DateIsNotAllowed : Throwable(), Error
        class DateInPast : Throwable(), Error
        class PriceChanged : Throwable(), Error
        class DurationChanged : Throwable(), Error
        class ServicesDoNotMatchOffer : Throwable(), Error
        class OfferAlreadyUsed : Throwable(), Error
        class ServiceNotFound : Throwable(), Error
        class EmployeeNotFound : Throwable(), Error
        class EmployeeSuspended : Throwable(), Error
    }
}
