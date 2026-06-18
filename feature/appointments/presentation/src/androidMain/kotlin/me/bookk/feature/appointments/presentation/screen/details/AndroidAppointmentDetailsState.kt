package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidDateTimePickerState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DateTimePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus

internal class AndroidAppointmentDetailsState : AppointmentDetailsState {
    override val appBar: AppBarState = AndroidAppBarState()

    override var status: UIAppointmentStatus by mutableStateOf(UIAppointmentStatus(AppointmentStatus.SCHEDULED))
    override val dateTimePicker: DateTimePickerState = AndroidDateTimePickerState()
    override val rescheduleButton: ButtonState = AndroidButtonState()
    override val infoSections: ListState<InfoLine> = AndroidListState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AppointmentDetailsDestination> = AndroidNavigationState()
}
