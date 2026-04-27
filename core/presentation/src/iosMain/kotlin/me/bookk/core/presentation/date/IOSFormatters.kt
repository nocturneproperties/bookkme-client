package me.bookk.core.presentation.date

import platform.Foundation.NSDateFormatter

internal class IOSFormatters(
    val dateTime: NSDateFormatter,
    val dateTimeSameYear: NSDateFormatter,
    val date: NSDateFormatter,
    val dateSameYear: NSDateFormatter,
    val time: NSDateFormatter
)