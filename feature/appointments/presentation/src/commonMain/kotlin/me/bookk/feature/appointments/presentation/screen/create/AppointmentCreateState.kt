package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerFieldState
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.uuid.Uuid

interface AppointmentCreateState {
    val appBar: AppBarState

    val clientPicker: PickerFieldState<SimplePickerPresentation<ClientSnapshot>>
    val servicePicker: MultiPickerState<ServicePickerPresentation>
    val datePicker: DatePickerFieldState
    val timePicker: TimePickerFieldState
    val note: TextFieldState

    val create: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentCreateDestination>
}

data class ServicePickerPresentation(
    override val pickerItemId: String,
    override val displayName: StringDesc,
    val duration: StringDesc,
    val price: String,
    val item: ServiceSnapshot
) : PickerPresentation() {
    companion object {
        fun stub() = ServicePickerPresentation(
            pickerItemId = "test" + Uuid.random().toString(),
            displayName = "Haircut".desc(),
            duration = "30 min".desc(),
            price = "$20",
            item = ServiceSnapshot.stub()
        )
    }
}
