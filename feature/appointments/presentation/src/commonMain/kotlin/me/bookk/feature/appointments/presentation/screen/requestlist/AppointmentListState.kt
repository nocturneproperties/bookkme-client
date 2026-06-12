package me.bookk.feature.appointments.presentation.screen.requestlist

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.feature.appointments.domain.api.entity.Appointment

interface AppointmentListState {
    val appBar: AppBarState
    var selectedDate: LocalDate
    val requests: ListState<AppointmentItemState>
    val refresh: RefreshState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentListDestinations>
}

class AppointmentItemState(
    val clientName: String,
    val serviceName: String,
    val scheduledAt: String,
    val earnings: String,
    val source: Appointment,
    val onItemClick: () -> Unit
) {

    constructor(
        appointment: Appointment,
        formatter: DateLocalizer.Formatter,
        onItemClick: () -> Unit
    ) : this(
        clientName = appointment.client.fullName,
        serviceName = appointment.services.joinToString { it.name },
        source = appointment,
        scheduledAt = formatter.format(appointment.date.toLocalDateTime(TimeZone.currentSystemDefault())),
        earnings = appointment.total,
        onItemClick = onItemClick
    )
}
