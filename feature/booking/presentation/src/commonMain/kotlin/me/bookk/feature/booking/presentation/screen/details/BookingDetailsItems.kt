package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.simple.InfoLine
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
            services = summary.services.mapIndexed { index, service -> service.toLine(index) },
            total = summary.total.toString().desc(),
            statusExplanation = status.explanation,
        )
    }

    private fun BookingSummary.rows(): List<InfoLine> {
        val strings = BookingRes.strings
        val noteLine = note.trim().takeIf { it.isNotEmpty() }?.let {
            InfoLine(NOTE_LINE_ID, strings.booking_details_note.desc(), it.desc())
        }
        return listOfNotNull(
            InfoLine(WHEN_LINE_ID, strings.booking_details_when.desc(), whenValue()),
            InfoLine(WITH_LINE_ID, strings.booking_details_with.desc(), specialistName.desc()),
            noteLine,
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

    private fun BookedService.toLine(index: Int): InfoLine {
        val label = when (quantity) {
            1 -> name.desc()
            else -> BookingRes.strings.booking_review_service_count.format(name, quantity)
        }
        return InfoLine("$SERVICE_LINE_ID_PREFIX$index", label, price.toString().desc())
    }

    private companion object {
        const val WHEN_LINE_ID = "when"
        const val WITH_LINE_ID = "with"
        const val NOTE_LINE_ID = "note"
        const val SERVICE_LINE_ID_PREFIX = "service_"
    }
}
