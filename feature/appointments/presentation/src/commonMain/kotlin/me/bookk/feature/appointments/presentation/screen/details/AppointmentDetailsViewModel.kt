package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDateTime
import library.device.api.DeviceFacade
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
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CancelAppointment.Error
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsState.Companion.APPOINTMENT_DATE_ID
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentDetailsViewModel(
    @InjectedParam private val appointmentId: Uuid,
    private val getAppointment: GetAppointment,
    private val cancelAppointment: CancelAppointment,
    private val updateAppointment: UpdateAppointment,
    private val dateLocalizer: DateLocalizer,
    private val device: DeviceFacade,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentDetailsState = stateFactory.createAppointmentDetailsState().setup()
    private var appointment = Appointment.stub(id = appointmentId)

    init {
        loadAppointment()
    }

    private fun loadAppointment() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointment(appointmentId) },
            onComplete = ::renderAppointment,
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onCancellationApproved(reason: String) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { cancelAppointment(appointmentId, appointment.businessId, reason) },
            onComplete = ::renderAppointment,
            onError = {
                when (it) {
                    is Error.AppointmentAlreadyCancelled -> {}
                    is Error.AppointmentAlreadyCompleted -> {}
                }
            }
        )
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
                onConfirm = weakSelfClosure { vm, reason -> vm.onCancellationApproved(reason) }
            )
        )
    }

    private fun onDatePicked(date: LocalDateTime) {
        val newAppointment = appointment.copy(date = date)
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.rescheduleButton.startLoading() },
            call = { updateAppointment(newAppointment) },
            onComplete = ::renderAppointment,
            onTerminate = { uiState.rescheduleButton.stopLoading() },
            onError = {
                when (it) {
                    is UpdateAppointment.Error.AppointmentOverlap -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                AppointmentsRes.strings.appointments_create_overlap_error.desc()
                            )
                        )
                    }

                    is UpdateAppointment.Error.DateIsNotAllowed -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                AppointmentsRes.strings.appointments_create_date_error.desc()
                            )
                        )
                    }

                    is UpdateAppointment.Error.TimeIsNotAllowed -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                AppointmentsRes.strings.appointments_create_time_error.desc()
                            )
                        )
                    }

                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun onRescheduleClick() {
        uiState.dateTimePicker.isDatePickerVisible = true
    }

    private fun onPhoneClick(phone: String) {
        if (phone.isNotEmpty()) {
            device.dial(phone)
        }
    }

    private fun onEmailClick(email: String) {
        if (email.isNotEmpty()) {
            device.mail(email)
        }
    }

    private fun renderAppointment(appointment: Appointment) {
        this.appointment = appointment
        uiState.appBar.title = appointment.client.fullName.desc()
        uiState.status = UIAppointmentStatus(appointment.status)
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
        uiState.dateTimePicker.pickedDate = appointment.date
        uiState.dateTimePicker.onDatePicked = weakSelfClosure { vm, v -> vm.onDatePicked(v) }
        uiState.rescheduleButton.isVisible = appointment.status == AppointmentStatus.SCHEDULED
        uiState.infoSections.replace(createSections(appointment))
    }

    private fun AppointmentDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick =
            weakSelfClosure { it.uiState.navigation.push(AppointmentDetailsDestination.Back) }

        rescheduleButton.onClick = weakSelfClosure { it.onRescheduleClick() }
        rescheduleButton.text = AppointmentsRes.strings.appointments_details_reschedule.desc()
    }

    private fun createSections(appointment: Appointment): List<InfoLine> {
        val dateFormat = dateLocalizer.forStyle(DateStyle.MEDIUM)
        return listOfNotNull(
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_cancellation_reason,
                value = appointment.cancellationReason
            ).takeIf { appointment.cancellationReason.isNotEmpty() },
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_phone,
                value = appointment.client.phone,
                onClick = weakSelfClosure { vm -> vm.onPhoneClick(appointment.client.phone) }
            ).takeIf { appointment.client.phone.isNotEmpty() },
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_email,
                value = appointment.client.email,
                onClick = weakSelfClosure { vm -> vm.onEmailClick(appointment.client.email) }
            ).takeIf { appointment.client.email.isNotEmpty() },
            InfoLine(
                title = AppointmentsRes.strings.appointments_create_date,
                value = dateFormat.format(appointment.date),
                id = APPOINTMENT_DATE_ID
            ),
            InfoLine(
                title = AppointmentsRes.strings.appointments_create_services,
                value = appointment.services.joinToString { it.name }
            ),
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_total,
                value = appointment.total
            ),
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_note,
                value = appointment.note
            ).takeIf { appointment.note.isNotEmpty() }
        )
    }
}
