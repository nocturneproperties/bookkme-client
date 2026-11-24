package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidAppointmentRequestState : AppointmentRequestState {
    override var title: StringDesc by mutableStateOf("".desc())
    override val requests: ListState<AppointmentRequestItemState> = AndroidListState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()

    override fun createAppointmentRequestItemState(): AppointmentRequestItemState {
        return AndroidAppointmentRequestItemState()
    }
}
