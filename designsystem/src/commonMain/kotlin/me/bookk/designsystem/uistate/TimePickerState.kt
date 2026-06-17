package me.bookk.designsystem.uistate

import kotlinx.datetime.LocalTime

interface TimePickerState {
    var isTimePickerVisible: Boolean
    var maxTime: LocalTime?
    var minTime: LocalTime?
    var pickedTime: LocalTime?
    var onTimePicked: ((LocalTime) -> Unit)?
}