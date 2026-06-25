package me.bookk.feature.appointments.presentation.screen.history

import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.presentation.screen.details.UIAppointmentStatus

interface AppointmentHistoryState {
    val appBar: AppBarState
    val searchField: TextFieldState
    val appointments: ListState<AppointmentHistoryItemState>
    val refresh: RefreshState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentHistoryDestinations>
}

class AppointmentHistoryItemState(
    val clientName: String,
    val serviceName: String,
    val scheduledAt: String,
    val earnings: String,
    val status: UIAppointmentStatus,
    val source: Appointment,
    val onItemClick: () -> Unit
) {

    @UsedInSwift
    val id = source.id

    constructor(
        appointment: Appointment,
        formatter: DateLocalizer.Formatter,
        onItemClick: () -> Unit
    ) : this(
        clientName = appointment.client.fullName,
        serviceName = appointment.services.joinToString { it.name },
        source = appointment,
        scheduledAt = formatter.format(appointment.date),
        earnings = appointment.total,
        status = UIAppointmentStatus(appointment.status),
        onItemClick = onItemClick
    )
}
