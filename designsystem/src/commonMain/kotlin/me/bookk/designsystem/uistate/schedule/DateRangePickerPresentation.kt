package me.bookk.designsystem.uistate.schedule

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.PickerPresentation

data class DateRangePickerPresentation(
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    override val displayName: StringDesc
) : PickerPresentation() {
    override val pickerItemId: String = dateFrom.toString() + dateTo.toString()

    constructor(dateFrom: LocalDate, dateTo: LocalDate, formatter: DateLocalizer.Formatter) : this(
        dateFrom = dateFrom,
        dateTo = dateTo,
        displayName = buildString {
            append(formatter.format(dateFrom, relative = true))
            if (dateFrom != dateTo) {
                append(" - ")
                append(formatter.format(dateTo, relative = true))
            }
        }.desc()
    )
}
