package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.InstantAppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.uuid.Uuid

internal class CreateAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource,
    private val createQuote: CreateQuote,
    private val getServices: GetServices,
    private val businesses: ObserveUserBusinessesChanges,
) : CreateAppointment {
    override suspend fun invoke(draft: AppointmentDraft): Appointment {
        if (draft.services.isEmpty()) throw CreateAppointment.Error.EmptyServices()
        val employeeId = draft.employeeId
            ?: businesses().first().firstOrNull { it.id == draft.businessId }?.employeeId
            ?: throw CreateAppointment.Error.NotAnEmployee()
        val quote = runCatching {
            createQuote(draft.businessId, draft.services.map { it.id }.distinct())
        }
            .getOrElse {
                if (it is CreateQuote.Error.ServiceNotFound) {
                    refreshAndThrow(draft.businessId, CreateAppointment.Error.ServiceNotFound())
                }
                throw it
            }
        draft.mismatch(quote)?.let { refreshAndThrow(draft.businessId, it) }
        val booking = InstantAppointmentDraft(
            businessId = draft.businessId,
            employeeId = employeeId,
            services = draft.services
                .groupingBy { it.id }
                .eachCount()
                .map { (id, count) -> RequestedService(id, count) },
            date = draft.date.toInstant(TimeZone.currentSystemDefault()),
            note = draft.note.trim(),
            offerToken = quote.token,
            clientId = draft.client.id,
        )
        return runCatching {
            appointmentDataSource.createAppointment(booking).also {
                appointmentDataSource.saveAppointmentInDB(it)
            }
        }
            .onBusinessError {
                val error = when (it.errorCode) {
                    AppointmentErrorCodes.APPOINTMENT_EXISTS -> {
                        CreateAppointment.Error.AppointmentOverlap()
                    }

                    AppointmentErrorCodes.TIME_NOT_ALLOWED -> {
                        CreateAppointment.Error.TimeIsNotAllowed()
                    }

                    AppointmentErrorCodes.DATE_NOT_ALLOWED -> {
                        CreateAppointment.Error.DateIsNotAllowed()
                    }

                    AppointmentErrorCodes.DATE_IN_PAST -> {
                        CreateAppointment.Error.DateInPast()
                    }

                    AppointmentErrorCodes.PRICE_CHANGED -> {
                        CreateAppointment.Error.PriceChanged()
                    }

                    AppointmentErrorCodes.DURATION_CHANGED -> {
                        CreateAppointment.Error.DurationChanged()
                    }

                    AppointmentErrorCodes.SERVICES_VALIDATION_FAILED -> {
                        CreateAppointment.Error.ServicesChanged()
                    }

                    AppointmentErrorCodes.QUOTE_TOKEN_ALREADY_USED -> {
                        CreateAppointment.Error.OfferAlreadyUsed()
                    }

                    AppointmentErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND -> {
                        CreateAppointment.Error.ServiceNotFound()
                    }

                    AppointmentErrorCodes.BUSINESS_QUOTE_EMPTY_SERVICE_LIST -> {
                        CreateAppointment.Error.EmptyServices()
                    }

                    AppointmentErrorCodes.BUSINESS_EMPLOYEE_NOT_EXISTS -> {
                        CreateAppointment.Error.EmployeeNotFound()
                    }

                    AppointmentErrorCodes.BUSINESS_CLIENT_NOT_EXISTS -> {
                        CreateAppointment.Error.ClientNotFound()
                    }

                    AppointmentErrorCodes.BUSINESS_EMPLOYEE_SUSPENDED -> {
                        CreateAppointment.Error.EmployeeSuspended()
                    }

                    else -> {
                        it
                    }
                }
                if (
                    error is CreateAppointment.Error.PriceChanged ||
                    error is CreateAppointment.Error.DurationChanged ||
                    error is CreateAppointment.Error.ServicesChanged ||
                    error is CreateAppointment.Error.ServiceNotFound
                ) {
                    refreshAndThrow(draft.businessId, error)
                }
                throw error
            }
            .getOrThrow()
    }

    private fun AppointmentDraft.mismatch(quote: Quote): Throwable? {
        val current = quote.services.associateBy { it.id }
        if (current.size != quote.services.size || current.keys != services.map { it.id }.toSet()) {
            return CreateAppointment.Error.ServicesChanged()
        }
        for (selected in services) {
            val service = current.getValue(selected.id)
            if (
                service.businessId != businessId ||
                !service.isAvailable ||
                service.name != selected.name ||
                service.group.id != selected.groupId
            ) {
                return CreateAppointment.Error.ServicesChanged()
            }
            if (service.price != selected.price) return CreateAppointment.Error.PriceChanged()
            if (service.duration != selected.duration) {
                return CreateAppointment.Error.DurationChanged()
            }
        }
        return null
    }

    private suspend fun refreshAndThrow(businessId: Uuid, error: Throwable): Nothing {
        getServices.refresh(businessId)
        throw error
    }
}
