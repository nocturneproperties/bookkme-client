package me.bookk.feature.appointments.presentation.screen.list

import kotlinx.datetime.LocalDate
import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface AppointmentListState {
    val appBar: AppBarState
    val requestsButton: ButtonState
    val datePicker: DatePickerState
    val dates: ListState<DateInfo>
    val appointments: ListState<AppointmentItemState>
    val refresh: RefreshState
    var requestsBusinessId: Uuid?
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
        scheduledAt = formatter.format(appointment.date.time),
        earnings = appointment.total,
        onItemClick = onItemClick
    )
}

class DateInfo(
    val date: LocalDate,
    val str: String,
    val isToday: Boolean
)
