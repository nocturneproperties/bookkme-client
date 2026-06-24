package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface DateRangePickerState : ViewState {
    var title: StringDesc
    val startDate: DatePickerFieldState
    val endDate: DatePickerFieldState
    var onDateRangeSelected: () -> Unit
}
