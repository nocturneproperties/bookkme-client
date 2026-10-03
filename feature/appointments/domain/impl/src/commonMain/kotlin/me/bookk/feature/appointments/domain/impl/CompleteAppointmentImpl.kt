package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.CompleteAppointment.Error
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class CompleteAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : CompleteAppointment {

    override suspend fun invoke(appointmentId: Uuid, priceAdjustment: PriceAdjustmentDraft?): Appointment {
        return runCatching {
            appointmentDataSource.completeAppointment(appointmentId, priceAdjustment).also {
                appointmentDataSource.saveAppointmentInDB(it)
            }
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED -> throw Error.AppointmentAlreadyCancelled()
                AppointmentErrorCodes.APPOINTMENT_NOT_STARTED -> throw Error.AppointmentNotStarted()
                AppointmentErrorCodes.APPOINTMENT_MARKED_NO_SHOW -> throw Error.AppointmentMarkedNoShow()
                AppointmentErrorCodes.PRICE_ADJUSTMENT_NEGATIVE_PRICE -> throw Error.NegativePrice()
                AppointmentErrorCodes.PRICE_ADJUSTMENT_CURRENCY_MISMATCH -> throw Error.CurrencyMismatch()
                AppointmentErrorCodes.PRICE_ADJUSTMENT_REASON_TOO_LONG -> throw Error.ReasonTooLong()
                AppointmentErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND -> throw Error.ServiceNotFound()
            }
        }.getOrThrow()
    }
}
