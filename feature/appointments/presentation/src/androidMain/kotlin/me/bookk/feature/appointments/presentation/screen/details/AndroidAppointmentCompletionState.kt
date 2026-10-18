package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidOptionsMultiPickerState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

internal class AndroidAppointmentCompletionState : AppointmentCompletionState {
    override var isSheetVisible: Boolean by mutableStateOf(false)
    override var title: StringDesc by mutableStateOf("".desc())
    override val servicePicker = AndroidOptionsMultiPickerState<ServicePickerPresentation>()
    override var servicesHint: StringDesc by mutableStateOf("".desc())
    override val price: TextFieldState = AndroidTextFieldState()
    override val note: TextFieldState = AndroidTextFieldState()
    override val confirmButton: ButtonState = AndroidButtonState()
}
