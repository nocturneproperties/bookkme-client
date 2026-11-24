package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDate
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.presentation.screen.BookingTotal
import me.bookk.feature.booking.presentation.screen.bookingDayFormat
import me.bookk.feature.booking.presentation.screen.bookingTotal
import me.bookk.feature.booking.presentation.screen.slotTimeFormat
import me.bookk.feature.booking.presentation.screen.units

internal class BookingSlotTotal(
    dateLocalizer: DateLocalizer,
) {

    private val dateFormat = dateLocalizer.bookingDayFormat()
    private val timeFormat = dateLocalizer.slotTimeFormat()

    fun total(services: List<PreparedService>, date: LocalDate, slot: BookingSlot?): BookingTotal? {
        val total = services.units().bookingTotal() ?: return null
        if (slot == null) return total
        return total.copy(
            summary = BookingRes.strings.booking_date_time_summary.format(
                dateFormat.format(date),
                timeFormat.format(slot.time),
            ),
        )
    }
}
