package me.bookk.core.presentation.date

import java.time.format.DateTimeFormatter

internal class AndroidFormatters(
    val dateTime: DateTimeFormatter,
    val dateTimeSameYear: DateTimeFormatter,
    val date: DateTimeFormatter,
    val dateSameYear: DateTimeFormatter,
    val time: DateTimeFormatter
)