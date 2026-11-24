package me.bookk.feature.booking.domain.api

import kotlinx.datetime.LocalDate
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import kotlin.uuid.Uuid

interface GetBookingSlots {
    suspend operator fun invoke(
        businessId: Uuid,
        timetable: BookingTimetable,
        from: LocalDate,
        to: LocalDate,
    ): List<BookingDay>

    sealed interface Error {
        class BookingUnavailable :
            Throwable(),
            Error
        class SelectionInvalid :
            Throwable(),
            Error
        class InvalidRange :
            Throwable(),
            Error
    }
}
