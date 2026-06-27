package me.bookk.core.presentation.date

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterLongStyle
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterShortStyle

actual enum class DateStyle(
    val dateModifier: NSDateFormatter.() -> Unit,
    val dateTimeModifier: NSDateFormatter.() -> Unit,
    val sameYearDateTimeModifier: NSDateFormatter.() -> Unit,
    val sameYearDateModifier: NSDateFormatter.() -> Unit,
    val timeModifier: NSDateFormatter.() -> Unit
) {
    SHORT(
        dateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterShortStyle)
        },
        dateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterShortStyle, timeStyle = NSDateFormatterShortStyle)
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterShortStyle, timeStyle = NSDateFormatterShortStyle)
        },
        sameYearDateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterShortStyle)
        },
        timeModifier = {
            matchToUserPreferences(timeStyle = NSDateFormatterShortStyle)
        }
    ),
    MEDIUM(
        dateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterMediumStyle)
        },
        dateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterMediumStyle, timeStyle = NSDateFormatterMediumStyle)
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterMediumStyle, timeStyle = NSDateFormatterMediumStyle)
        },
        sameYearDateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterMediumStyle)
        },
        timeModifier = {
            matchToUserPreferences(timeStyle = NSDateFormatterMediumStyle)
        }
    ),
    LONG(
        dateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterLongStyle)
        },
        dateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterLongStyle, timeStyle = NSDateFormatterLongStyle)
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterLongStyle, timeStyle = NSDateFormatterLongStyle)
        },
        sameYearDateModifier = {
            matchToUserPreferences(dateStyle = NSDateFormatterLongStyle)
        },
        timeModifier = {
            matchToUserPreferences(timeStyle = NSDateFormatterLongStyle)
        }
    ),
    D_MMM_YYYY_RELATIVE(
        dateModifier = {
            matchToUserPreferences("d MMM yyyy")
        },
        dateTimeModifier = {
            matchToUserPreferences("d MMM yyyy HH:mm")
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences("d MMM HH:mm")
        },
        sameYearDateModifier = {
            matchToUserPreferences("d MMM")
        },
        timeModifier = {
            matchToUserPreferences("HH:mm")
        }
    ),
    NARROW_WEEKDAY(
        dateModifier = {
            matchToUserPreferences("EEEEE")
        },
        dateTimeModifier = {
            matchToUserPreferences("EEEEE HH:mm")
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences("EEEEE HH:mm")
        },
        sameYearDateModifier = {
            matchToUserPreferences("EEEEE")
        },
        timeModifier = {
            matchToUserPreferences("HH:mm")
        }
    ),
    FULL_WEEKDAY(
        dateModifier = {
            matchToUserPreferences("EEEE")
        },
        dateTimeModifier = {
            matchToUserPreferences("EEEE HH:mm")
        },
        sameYearDateTimeModifier = {
            matchToUserPreferences("EEEE HH:mm")
        },
        sameYearDateModifier = {
            matchToUserPreferences("EEEE")
        },
        timeModifier = {
            matchToUserPreferences("HH:mm")
        }
    )
}