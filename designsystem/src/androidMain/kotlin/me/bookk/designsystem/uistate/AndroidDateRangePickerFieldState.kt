package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate

@Immutable
class AndroidDateRangePickerFieldState(
    textFieldState: TextFieldState,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    onClick: (() -> Unit)? = null
) : AndroidViewState(isVisible = true), DateRangePickerFieldState {
    override val textField: TextFieldState = textFieldState
    override var startDate: LocalDate? by mutableStateOf(startDate)
    override var endDate: LocalDate? by mutableStateOf(endDate)
    override var onClick: (() -> Unit)? by mutableStateOf(onClick)
}
