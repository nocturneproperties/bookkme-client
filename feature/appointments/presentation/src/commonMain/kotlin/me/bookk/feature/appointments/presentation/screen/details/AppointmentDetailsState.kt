package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DateTimePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

interface AppointmentDetailsState {
    val appBar: AppBarState
    var status: UIAppointmentStatus
    val dateTimePicker: DateTimePickerState
    val rescheduleButton: ButtonState
    val completeButton: ButtonState
    val noShowButton: ButtonState
    val infoSections: ListState<InfoLine>
    val servicePicker: OptionsMultiPickerState<ServicePickerPresentation>
    var subtotalLabel: StringDesc
    var subtotalPrice: String

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentDetailsDestination>
}

class UIAppointmentStatus(
    val label: StringDesc,
    val color: ColorToken
) {
    internal constructor(status: AppointmentStatus) : this(
        label = status.label(),
        color = status.color()
    )
}

private fun AppointmentStatus.label(): StringDesc = when (this) {
    AppointmentStatus.SCHEDULED -> AppointmentsRes.strings.appointments_status_scheduled.desc()
    AppointmentStatus.COMPLETED -> AppointmentsRes.strings.appointments_status_completed.desc()
    AppointmentStatus.CANCELLED -> AppointmentsRes.strings.appointments_status_cancelled.desc()
    AppointmentStatus.NO_SHOW -> AppointmentsRes.strings.appointments_status_no_show.desc()
}

private fun AppointmentStatus.color(): ColorToken = when (this) {
    AppointmentStatus.SCHEDULED -> ColorToken.ActionText
    AppointmentStatus.COMPLETED -> ColorToken.Success
    AppointmentStatus.CANCELLED -> ColorToken.Error
    AppointmentStatus.NO_SHOW -> ColorToken.SecondaryText
}
