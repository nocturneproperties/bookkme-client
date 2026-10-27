package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import kotlin.uuid.Uuid

interface CompleteAppointment {
    suspend operator fun invoke(appointmentId: Uuid, priceAdjustment: PriceAdjustmentDraft? = null): Appointment

    sealed interface Error {
        class AppointmentAlreadyCancelled : Throwable(), Error
        class AppointmentNotStarted : Throwable(), Error
        class AppointmentMarkedNoShow : Throwable(), Error
        class NegativePrice : Throwable(), Error
        class CurrencyMismatch : Throwable(), Error
        class ReasonTooLong : Throwable(), Error
        class ServiceNotFound : Throwable(), Error
    }
}
