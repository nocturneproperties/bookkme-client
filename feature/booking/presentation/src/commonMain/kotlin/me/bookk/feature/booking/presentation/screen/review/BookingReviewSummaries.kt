package me.bookk.feature.booking.presentation.screen.review

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.presentation.screen.bookingDayFormat
import me.bookk.feature.booking.presentation.screen.fullName
import me.bookk.feature.booking.presentation.screen.slotTimeFormat
import me.bookk.feature.booking.presentation.screen.totalDuration
import me.bookk.feature.booking.presentation.screen.totalPrice
import me.bookk.feature.booking.presentation.screen.units

internal class BookingReviewSummaries(
    dateLocalizer: DateLocalizer,
) {

    private val dayFormat = dateLocalizer.bookingDayFormat()
    private val timeFormat = dateLocalizer.slotTimeFormat()

    fun summary(timetable: BookingTimetable, preparation: BookingPreparation): ReviewSummary? {
        val start = preparation.slotStart ?: return null
        val units = preparation.services.units()
        if (units.isEmpty()) return null
        val startsAt = start.toLocalDateTime(timetable.timeZone)
        val endsAt = (start + units.totalDuration()).toLocalDateTime(timetable.timeZone)
        return ReviewSummary(
            day = dayFormat.format(startsAt.date).desc(),
            details = details(timetable, startsAt, endsAt),
            services = preparation.services.map { it.toItem() },
            total = units.totalPrice().toString().desc(),
        )
    }

    private fun details(
        timetable: BookingTimetable,
        startsAt: LocalDateTime,
        endsAt: LocalDateTime,
    ): StringDesc {
        val timeRange = BookingRes.strings.booking_review_time_range.format(
            timeFormat.format(startsAt.time),
            timeFormat.format(endsAt.time),
        )
        return when (val specialist = timetable.specialist) {
            null -> {
                timeRange
            }

            else -> {
                BookingRes.strings.booking_review_details.format(timeRange, specialist.fullName())
            }
        }
    }

    private fun PreparedService.toItem(): ReviewServiceItem {
        return ReviewServiceItem(
            id = service.id.toString(),
            name = name(),
            price = (service.price * quantity).toString().desc(),
            previousPrice = previousPrice(),
        )
    }

    private fun PreparedService.name(): StringDesc {
        return when (quantity) {
            1 -> service.name.desc()
            else -> BookingRes.strings.booking_review_service_count.format(service.name, quantity)
        }
    }

    private fun PreparedService.previousPrice(): StringDesc? {
        if (approvedPrice == service.price) return null
        return (approvedPrice * quantity).toString().desc()
    }
}
