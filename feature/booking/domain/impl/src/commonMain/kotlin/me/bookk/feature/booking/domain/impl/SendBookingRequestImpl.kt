package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.first
import library.money.api.Money
import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.SendBookingRequest
import me.bookk.feature.booking.domain.api.SendBookingRequest.Error
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class SendBookingRequestImpl(
    private val dataSource: BookingPreparationDataSource,
    private val getBookingPreparation: GetBookingPreparation,
    private val createQuote: CreateQuote,
    private val createAppointmentRequest: CreateAppointmentRequest,
    private val getBusinessServices: GetBusinessServices,
    private val getUpcomingBookings: GetUpcomingBookings,
) : SendBookingRequest {

    override suspend fun invoke(businessId: Uuid, note: String): SentBookingRequest {
        val preparation = shownPreparation(businessId)
        val draft = preparation.draft(businessId, note.trim()) ?: throw Error.SelectionIncomplete()
        runSuspendCatching { send(preparation, draft) }
            .onFailure { error ->
                throw error.toBookingError().also { refreshServicesIfChanged(businessId, it) }
            }
        dataSource.saveSelection(businessId, BookingSelection())
        runSuspendCatching { getUpcomingBookings.refresh() }
        return SentBookingRequest(businessId, draft.employeeId, draft.date)
    }

    private suspend fun shownPreparation(businessId: Uuid): BookingPreparation {
        val selection = dataSource.observeSelection(businessId).first()
        val preparation = getBookingPreparation.flow(businessId).first()
        if (preparation.services.size != selection.services.size) throw Error.ServicesChanged()
        return preparation
    }

    private fun BookingPreparation.draft(businessId: Uuid, note: String): AppointmentRequestDraft? {
        val specialistId = specialistId ?: return null
        val slotStart = slotStart ?: return null
        if (services.isEmpty()) return null
        return AppointmentRequestDraft(
            businessId = businessId,
            employeeId = specialistId,
            services = services.map { RequestedService(it.service.id, it.quantity) },
            date = slotStart,
            note = note,
            offerToken = "",
        )
    }

    private suspend fun send(preparation: BookingPreparation, draft: AppointmentRequestDraft) {
        val quote = createQuote(draft.businessId, preparation.quotedServiceIds())
        quote.checkAgainst(preparation)
        createAppointmentRequest(draft.copy(offerToken = quote.token))
    }

    private fun BookingPreparation.quotedServiceIds(): List<Uuid> {
        return services.flatMap { prepared -> List(prepared.quantity) { prepared.service.id } }
    }

    private fun Quote.checkAgainst(preparation: BookingPreparation) {
        val quoted = services.groupBy { it.id }
        val sameServices = quoted.size == preparation.services.size &&
            preparation.services.all { it.isBookableAs(quoted[it.service.id].orEmpty()) }
        if (!sameServices) throw Error.ServicesChanged()
        val samePrices = services.all { it.price == preparation.priceOf(it.id) }
        if (!samePrices) throw Error.PricesChanged()
    }

    private fun PreparedService.isBookableAs(quoted: List<Service>): Boolean {
        return quoted.size == quantity &&
            quoted.all { it.isAvailable && it.duration == service.duration }
    }

    private fun BookingPreparation.priceOf(serviceId: Uuid): Money? {
        return services.firstOrNull { it.service.id == serviceId }?.service?.price
    }

    private fun Throwable.toBookingError(): Throwable {
        return when (this) {
            is CreateAppointmentRequest.Error.EmployeeBusy,
            is CreateAppointmentRequest.Error.TimeIsNotAllowed,
            is CreateAppointmentRequest.Error.DateIsNotAllowed,
            is CreateAppointmentRequest.Error.EmployeeDateIsNotAllowed,
            is CreateAppointmentRequest.Error.EmployeeTimeIsNotAllowed,
            is CreateAppointmentRequest.Error.DateInPast,
            -> {
                Error.TimeUnavailable()
            }

            is CreateAppointmentRequest.Error.PendingRequestsLimitReached -> {
                Error.PendingRequestsLimitReached()
            }

            is CreateAppointmentRequest.Error.PriceChanged -> {
                Error.PricesChanged()
            }

            is CreateQuote.Error.ServiceNotFound,
            is CreateAppointmentRequest.Error.DurationChanged,
            is CreateAppointmentRequest.Error.ServicesDoNotMatchOffer,
            is CreateAppointmentRequest.Error.OfferAlreadyUsed,
            is CreateAppointmentRequest.Error.ServiceNotFound,
            -> {
                Error.ServicesChanged()
            }

            is CreateAppointmentRequest.Error.EmployeeNotFound,
            is CreateAppointmentRequest.Error.EmployeeSuspended,
            -> {
                Error.SpecialistUnavailable()
            }

            else -> {
                this
            }
        }
    }

    private suspend fun refreshServicesIfChanged(businessId: Uuid, error: Throwable) {
        if (error !is Error.ServicesChanged && error !is Error.PricesChanged) return
        runSuspendCatching { getBusinessServices.refresh(businessId) }
    }
}
