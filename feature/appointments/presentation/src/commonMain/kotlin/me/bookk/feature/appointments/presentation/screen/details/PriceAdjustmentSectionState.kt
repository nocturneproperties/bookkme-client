package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

interface PriceAdjustmentSectionState {
    var isVisible: Boolean
    var reasonTitle: StringDesc
    var reason: StringDesc?
    val servicePicker: OptionsMultiPickerState<ServicePickerPresentation>
}
