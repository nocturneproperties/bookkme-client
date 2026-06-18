package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalDateTime

interface DateTimePickerState {
    var isDatePickerVisible: Boolean
    var pickedDate: LocalDateTime?
    var maxDate: LocalDateTime?
    var minDate: LocalDateTime?
    var onDatePicked: ((LocalDateTime) -> Unit)?
}