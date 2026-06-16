package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentDetailsViewModel(
    @InjectedParam private val appointmentId: Uuid,
    private val getAppointment: GetAppointment,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentDetailsState = stateFactory.createAppointmentDetailsState().setup()

    init {
        loadAppointment()
    }

    private fun cancelAppointment(reason: String) {

    }

    private fun onCancelClick() {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = DesignSystem.strings.action_confirm.desc(),
                message = AppointmentsRes.strings.appointments_details_cancel.desc(),
                placeholder = AppointmentsRes.strings.appointments_details_cancel_reason.desc(),
                cancelText = DesignSystem.strings.action_ignore.desc(),
                confirmText = DesignSystem.strings.action_cancel.desc(),
                confirmActionType = ActionType.NEGATIVE,
                onConfirm = weakSelfClosure { vm, reason -> vm.cancelAppointment(reason) }
            )
        )
    }

    private fun loadAppointment() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointment(appointmentId) },
            onComplete = { appointment ->
                val dateFormat = dateLocalizer.forStyle(DateStyle.MEDIUM)
                val dateTime = appointment.date.toLocalDateTime(TimeZone.currentSystemDefault())

                uiState.appBar.title = appointment.client.fullName.desc()
                if (appointment.status == AppointmentStatus.SCHEDULED) {
                    uiState.appBar.actions.replace(
                        listOf(
                            AppBarAction(
                                contentDescription = DesignSystem.strings.action_cancel.desc(),
                                type = ActionType.NEGATIVE,
                                onClick = weakSelfClosure { it.onCancelClick() }
                            )
                        )
                    )
                }
                uiState.infoSections.replace(
                    listOf(
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_details_phone.desc(),
                            value = appointment.client.phone.ifBlank { "-" }.desc()
                        ),
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_details_email.desc(),
                            value = appointment.client.email.ifBlank { "-" }.desc()
                        ),
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_create_date.desc(),
                            value = dateFormat.format(dateTime).desc()
                        ),
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_create_services.desc(),
                            value = appointment.services.joinToString { it.name }.desc()
                        ),
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_details_total.desc(),
                            value = appointment.total.desc()
                        ),
                        InfoLine(
                            title = AppointmentsRes.strings.appointments_details_note.desc(),
                            value = appointment.note.ifBlank { "-" }.desc()
                        )
                    )
                )
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun AppointmentStatus.label(): StringDesc = when (this) {
        AppointmentStatus.SCHEDULED -> AppointmentsRes.strings.appointments_status_scheduled.desc()
        AppointmentStatus.COMPLETED -> AppointmentsRes.strings.appointments_status_completed.desc()
        AppointmentStatus.CANCELLED -> AppointmentsRes.strings.appointments_status_cancelled.desc()
    }

    private fun AppointmentDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick =
            weakSelfClosure { it.uiState.navigation.push(AppointmentDetailsDestination.Back) }
    }
}
