package me.bookk.feature.booking.presentation.screen.bookings

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.joinToStringDesc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDate
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import me.bookk.feature.booking.presentation.screen.label
import me.bookk.feature.booking.presentation.screen.slotTimeFormat

internal class BookingsRows(
    dateLocalizer: DateLocalizer,
) {

    private val weekdayFormat = dateLocalizer.strict(WEEKDAY_PATTERN, respectUserSettings = true)
    private val monthFormat = dateLocalizer.strict(MONTH_PATTERN, respectUserSettings = true)
    private val monthYearFormat = dateLocalizer.strict(
        MONTH_YEAR_PATTERN,
        respectUserSettings = true,
    )
    private val timeFormat = dateLocalizer.slotTimeFormat()

    fun items(
        period: BookingPeriod,
        bookings: List<BookingOverview>,
        today: LocalDate,
        onClick: (BookingOverview) -> Unit,
    ): List<BookingsListItem> {
        return bookings
            .groupBy { it.monthStart() }
            .flatMap { (month, monthBookings) ->
                listOf(monthItem(month, today)) + monthBookings.map { it.toItem(period, onClick) }
            }
    }

    private fun BookingOverview.monthStart(): LocalDate {
        val date = summary.startsAt.date
        return LocalDate(date.year, date.month, 1)
    }

    private fun monthItem(month: LocalDate, today: LocalDate): BookingsListItem.Month {
        val format = if (month.year == today.year) monthFormat else monthYearFormat
        return BookingsListItem.Month(key = "$MONTH_KEY_PREFIX$month", title = format.format(month))
    }

    private fun BookingOverview.toItem(
        period: BookingPeriod,
        onClick: (BookingOverview) -> Unit,
    ): BookingsListItem.Booking {
        val label = status.label()
        val startsAt = summary.startsAt
        return BookingsListItem.Booking(
            key = id.toString(),
            day = startsAt.day.toString(),
            weekday = weekdayFormat.format(startsAt.date),
            dayColor = period.dayColor(),
            businessName = summary.businessName,
            details = BookingRes.strings.booking_review_details.format(
                timeFormat.format(startsAt.time),
                summary.services.names(),
            ),
            status = label.text,
            statusColor = label.color,
            onClick = { onClick(this) },
        )
    }

    private fun BookingPeriod.dayColor(): ColorToken {
        return when (this) {
            BookingPeriod.UPCOMING -> ColorToken.PrimaryText
            BookingPeriod.PAST -> ColorToken.SecondaryText
        }
    }

    private fun List<BookedService>.names(): StringDesc {
        return map { it.name() }.joinToStringDesc(SERVICE_SEPARATOR)
    }

    private fun BookedService.name(): StringDesc {
        return when (quantity) {
            1 -> name.desc()
            else -> BookingRes.strings.booking_review_service_count.format(name, quantity)
        }
    }

    private companion object {
        const val WEEKDAY_PATTERN = "EEE"
        const val MONTH_PATTERN = "LLLL"
        const val MONTH_YEAR_PATTERN = "LLLL yyyy"
        const val MONTH_KEY_PREFIX = "month-"
        const val SERVICE_SEPARATOR = ", "
    }
}
