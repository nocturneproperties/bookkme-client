package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalDate

interface DatePickerState {
    var isDatePickerVisible: Boolean
    var pickedDate: LocalDate?
    var maxDate: LocalDate?
    var minDate: LocalDate?
    var onDatePicked: ((LocalDate) -> Unit)?
}