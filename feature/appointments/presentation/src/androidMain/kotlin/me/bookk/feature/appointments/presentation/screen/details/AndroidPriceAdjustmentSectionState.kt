package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidOptionsMultiPickerState
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

internal class AndroidPriceAdjustmentSectionState : PriceAdjustmentSectionState {
    override var isVisible: Boolean by mutableStateOf(false)
    override var reasonTitle: StringDesc by mutableStateOf("".desc())
    override var reason: StringDesc? by mutableStateOf(null)
    override val servicePicker = AndroidOptionsMultiPickerState<ServicePickerPresentation>()
}
