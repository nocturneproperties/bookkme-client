package me.bookk.feature.booking.presentation.screen

import me.bookk.core.presentation.date.DateLocalizer

internal fun DateLocalizer.bookingDayFormat(): DateLocalizer.Formatter {
    return strict(BOOKING_DAY_PATTERN, respectUserSettings = true)
}

internal fun DateLocalizer.slotTimeFormat(): DateLocalizer.Formatter {
    return strict(SLOT_TIME_PATTERN, respectUserSettings = true)
}

private const val BOOKING_DAY_PATTERN = "EEE d MMM"
private const val SLOT_TIME_PATTERN = "HH:mm"
