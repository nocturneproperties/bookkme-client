package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.ButtonState

internal class AndroidAppointmentRequestItemState :
    AndroidViewState(),
    AppointmentRequestItemState {
    override var clientName: String by mutableStateOf("")
    override var clientInitials: String by mutableStateOf("")
    override var schedule: StringDesc by mutableStateOf("".desc())
    override var services: List<StringDesc> by mutableStateOf(emptyList())
    override var price: String by mutableStateOf("")
    override var note: String by mutableStateOf("")
    override var onCallClick: (() -> Unit)? by mutableStateOf(null)
    override val approveButton: ButtonState = AndroidButtonState()
    override val declineButton: ButtonState = AndroidButtonState()
}
