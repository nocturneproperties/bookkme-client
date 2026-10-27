package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

interface AppointmentCompletionState {
    var isSheetVisible: Boolean
    var title: StringDesc
    val servicePicker: OptionsMultiPickerState<ServicePickerPresentation>
    var servicesHint: StringDesc
    val price: TextFieldState
    val note: TextFieldState
    val confirmButton: ButtonState
}
