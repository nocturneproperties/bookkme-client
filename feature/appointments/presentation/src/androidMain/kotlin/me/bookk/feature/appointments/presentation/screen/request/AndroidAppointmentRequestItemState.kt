package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.ButtonState

internal class AndroidAppointmentRequestItemState : AndroidViewState(), AppointmentRequestItemState {
    override var clientName: String by mutableStateOf("")
    override var serviceName: String by mutableStateOf("")
    override var scheduledDate: String by mutableStateOf("")
    override var scheduledTime: String by mutableStateOf("")
    override var note: String by mutableStateOf("")
    override var earnings: String by mutableStateOf("")
    override val approveButton: ButtonState = AndroidButtonState()
    override val declineButton: ButtonState = AndroidButtonState()
}
