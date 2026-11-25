package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import me.bookk.feature.booking.presentation.screen.bookingDayFormat
import me.bookk.feature.booking.presentation.screen.slotTimeFormat

internal class BookingStatusPresentation(
    val label: StringDesc,
    val color: ColorToken,
    val explanation: StringDesc,
)

internal class BookingDetailsItems(
    dateLocalizer: DateLocalizer,
) {

    private val dayFormat = dateLocalizer.bookingDayFormat()
    private val timeFormat = dateLocalizer.slotTimeFormat()

    fun item(summary: BookingSummary, status: BookingStatusPresentation): BookingDetailsItem {
        return BookingDetailsItem(
            businessName = summary.businessName.desc(),
            status = status.label,
            statusColor = status.color,
            rows = summary.rows(),
            services = summary.services.map { it.toItem() },
            total = summary.total.toString().desc(),
            statusExplanation = status.explanation,
        )
    }

    private fun BookingSummary.rows(): List<BookingDetailsRow> {
        val strings = BookingRes.strings
        val noteRow = note.trim().takeIf { it.isNotEmpty() }?.let {
            BookingDetailsRow(strings.booking_details_note.desc(), it.desc())
        }
        return listOfNotNull(
            BookingDetailsRow(strings.booking_details_when.desc(), whenValue()),
            BookingDetailsRow(strings.booking_details_with.desc(), specialistName.desc()),
            noteRow,
        )
    }

    private fun BookingSummary.whenValue(): StringDesc {
        val strings = BookingRes.strings
        val timeRange = strings.booking_review_time_range.format(
            timeFormat.format(startsAt.time),
            timeFormat.format(endsAt.time),
        )
        return strings.booking_details_when_value.format(dayFormat.format(startsAt.date), timeRange)
    }

    private fun BookedService.toItem(): BookingDetailsServiceItem {
        val label = when (quantity) {
            1 -> name.desc()
            else -> BookingRes.strings.booking_review_service_count.format(name, quantity)
        }
        return BookingDetailsServiceItem(label, price.toString().desc())
    }
}
