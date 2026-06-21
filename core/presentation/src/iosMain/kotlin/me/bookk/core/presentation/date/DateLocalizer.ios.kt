package me.bookk.core.presentation.date

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterLongStyle
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
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
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterNoStyle
        },
        dateTimeModifier = {
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterShortStyle
        },
        sameYearDateTimeModifier = {
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterShortStyle
        },
        sameYearDateModifier = {
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterNoStyle
        },
        timeModifier = {
            dateStyle = NSDateFormatterNoStyle
            timeStyle = NSDateFormatterShortStyle
        }
    ),
    MEDIUM(
        dateModifier = {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterNoStyle
        },
        dateTimeModifier = {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterMediumStyle
        },
        sameYearDateTimeModifier = {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterMediumStyle
        },
        sameYearDateModifier = {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterNoStyle
        },
        timeModifier = {
            dateStyle = NSDateFormatterNoStyle
            timeStyle = NSDateFormatterMediumStyle
        }
    ),
    LONG(
        dateModifier = {
            dateStyle = NSDateFormatterLongStyle
            timeStyle = NSDateFormatterNoStyle
        },
        dateTimeModifier = {
            dateStyle = NSDateFormatterLongStyle
            timeStyle = NSDateFormatterLongStyle
        },
        sameYearDateTimeModifier = {
            dateStyle = NSDateFormatterLongStyle
            timeStyle = NSDateFormatterLongStyle
        },
        sameYearDateModifier = {
            dateStyle = NSDateFormatterLongStyle
            timeStyle = NSDateFormatterNoStyle
        },
        timeModifier = {
            dateStyle = NSDateFormatterNoStyle
            timeStyle = NSDateFormatterLongStyle
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