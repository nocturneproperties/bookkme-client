package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalDate

interface DateRangePickerFieldState : ViewState {
    val textField: TextFieldState
    var startDate: LocalDate?
    var endDate: LocalDate?
    var onClick: (() -> Unit)?

    val hasPeriodSelected: Boolean
        get() = startDate != null && endDate != null
}
