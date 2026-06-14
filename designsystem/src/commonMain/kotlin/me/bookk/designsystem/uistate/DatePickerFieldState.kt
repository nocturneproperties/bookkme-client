package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalDate

interface DatePickerFieldState : ViewState {
    val textField: TextFieldState
    var pickedDate: LocalDate?
    var maxDate: LocalDate?
    var minDate: LocalDate?
    var onDatePicked: ((LocalDate) -> Unit)?
}