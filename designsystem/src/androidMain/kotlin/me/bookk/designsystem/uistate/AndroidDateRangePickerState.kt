package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem

class AndroidDateRangePickerState(
    title: StringDesc = "".desc(),
) : AndroidViewState(isVisible = true), DateRangePickerState {
    override var title: StringDesc by mutableStateOf(title)
    override val startDate: DatePickerFieldState = AndroidDatePickerFieldState().apply {
        textField.placeholder = DesignSystem.strings.common_from.desc()
    }
    override val endDate: DatePickerFieldState = AndroidDatePickerFieldState().apply {
        textField.placeholder = DesignSystem.strings.common_to.desc()
    }
    override var onDateRangeSelected: () -> Unit by mutableStateOf({})
}
