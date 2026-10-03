package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDateTime
import library.device.api.DeviceFacade
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.dashOnBlank
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CancelAppointment.Error
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentDetailsViewModel(
    @InjectedParam private val appointmentId: Uuid,
    private val getAppointment: GetAppointment,
    private val cancelAppointment: CancelAppointment,
    private val updateAppointment: UpdateAppointment,
    private val completeAppointment: CompleteAppointment,
    private val markAppointmentNoShow: MarkAppointmentNoShow,
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
                    is Error.AppointmentAlreadyCancelled -> {
                        renderAppointment(appointment.copy(status = AppointmentStatus.CANCELLED))
                    }
                    is Error.AppointmentAlreadyCompleted -> {
                        renderAppointment(appointment.copy(status = AppointmentStatus.COMPLETED))
                    }
                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun onCompleteClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.completeButton.startLoading() },
            call = { completeAppointment(appointmentId) },
            onComplete = ::renderAppointment,
            onTerminate = { uiState.completeButton.stopLoading() },
            onError = {
                when (it) {
                    is CompleteAppointment.Error.AppointmentAlreadyCancelled -> renderStatus(AppointmentStatus.CANCELLED)
                    is CompleteAppointment.Error.AppointmentMarkedNoShow -> renderStatus(AppointmentStatus.NO_SHOW)
                    is CompleteAppointment.Error.AppointmentNotStarted -> showMessage(
                        AppointmentsRes.strings.appointments_details_not_started_error.desc()
                    )
                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun onNoShowClick() {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = DesignSystem.strings.action_confirm.desc(),
                message = AppointmentsRes.strings.appointments_details_no_show_confirm.desc(),
                buttons = listOf(
                    ButtonDescriptor(DesignSystem.strings.action_cancel.desc(), actionType = ActionType.CANCEL),
                    ButtonDescriptor(
                        AppointmentsRes.strings.appointments_details_no_show_action.desc(),
                        actionType = ActionType.NEGATIVE,
                        onClick = weakVMClosure { it.onNoShowApproved() }
                    )
                )
            )
        )
    }

    private fun onNoShowApproved() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.noShowButton.startLoading() },
            call = { markAppointmentNoShow(appointmentId) },
            onComplete = ::renderAppointment,
            onTerminate = { uiState.noShowButton.stopLoading() },
            onError = {
                when (it) {
                    is MarkAppointmentNoShow.Error.AppointmentAlreadyCancelled -> renderStatus(AppointmentStatus.CANCELLED)
                    is MarkAppointmentNoShow.Error.AppointmentNotStarted -> showMessage(
                        AppointmentsRes.strings.appointments_details_not_started_error.desc()
                    )
                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun renderStatus(status: AppointmentStatus) {
        renderAppointment(appointment.copy(status = status))
    }

    private fun showMessage(message: StringDesc) {
        uiState.notifications.add(PresentationNotification.Message.simple(message))
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
                onConfirm = weakVMClosure { vm, reason -> vm.onCancellationApproved(reason) }
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

                    is UpdateAppointment.Error.AppointmentNotScheduled -> showMessage(
                        AppointmentsRes.strings.appointments_details_not_scheduled_error.desc()
                    )

                    is UpdateAppointment.Error.EmployeeSuspended -> showMessage(
                        AppointmentsRes.strings.appointments_details_employee_suspended_error.desc()
                    )

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
        uiState.appBar.actions.replace(appBarActions(appointment))
        uiState.dateTimePicker.pickedDate = appointment.date
        uiState.dateTimePicker.onDatePicked = weakVMClosure { vm, v -> vm.onDatePicked(v) }
        uiState.rescheduleButton.isVisible = appointment.status == AppointmentStatus.SCHEDULED
        uiState.completeButton.isVisible = appointment.canBeCompleted()
        uiState.noShowButton.isVisible = appointment.canBeMarkedNoShow()
        uiState.infoSections.replace(createSections(appointment))
        renderServices(appointment)
    }

    private fun renderServices(appointment: Appointment) {
        uiState.servicePicker.isVisible = true
        uiState.servicePicker.replaceSelected(
            appointment.services.mapIndexed { index, service ->
                ServicePickerPresentation(service).copy(pickerItemId = "${service.id}_$index")
            }
        )
        uiState.subtotalLabel = AppointmentsRes.strings.appointments_create_subtotal.format(appointment.services.size)
        uiState.subtotalPrice = appointment.total
    }

    private fun AppointmentDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick =
            weakVMClosure { it.uiState.navigation.push(AppointmentDetailsDestination.Back) }


        rescheduleButton.onClick = weakVMClosure { it.onRescheduleClick() }
        rescheduleButton.text = AppointmentsRes.strings.appointments_details_reschedule.desc()

        completeButton.onClick = weakVMClosure { it.onCompleteClick() }
        completeButton.text = AppointmentsRes.strings.appointments_details_complete.desc()

        noShowButton.onClick = weakVMClosure { it.onNoShowClick() }
        noShowButton.text = AppointmentsRes.strings.appointments_details_no_show.desc()

        servicePicker.isEditable = false
        servicePicker.isVisible = false
        servicePicker.pickerTitle = AppointmentsRes.strings.appointments_create_services.desc()
    }

    private fun appBarActions(appointment: Appointment): List<AppBarAction> {
        if (appointment.status != AppointmentStatus.SCHEDULED) return emptyList()
        return listOf(
            AppBarAction(
                contentDescription = DesignSystem.strings.action_cancel.desc(),
                type = ActionType.NEGATIVE,
                onClick = weakVMClosure { it.onCancelClick() }
            )
        )
    }

    private fun dateLine(appointment: Appointment): InfoLine {
        return InfoLine(
            title = AppointmentsRes.strings.appointments_create_date,
            value = dateLocalizer.forStyle(DateStyle.MEDIUM).format(appointment.date, relative = true)
        )
    }

    private fun createSections(appointment: Appointment): List<InfoLine> {
        return listOfNotNull(
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_cancellation_reason,
                value = appointment.cancellationReason
            ).takeIf { appointment.cancellationReason.isNotEmpty() },
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_phone,
                value = appointment.client.phone.dashOnBlank(),
                onClick = weakVMClosure { vm -> vm.onPhoneClick(appointment.client.phone!!) }
            ).takeIf { !appointment.client.phone.isNullOrBlank() },
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_email,
                value = appointment.client.email.dashOnBlank(),
                onClick = weakVMClosure { vm -> vm.onEmailClick(appointment.client.email!!) }
            ).takeIf { !appointment.client.email.isNullOrBlank() },
            dateLine(appointment),
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_note,
                value = appointment.note
            ).takeIf { appointment.note.isNotEmpty() }
        ) + completionSections(appointment)
    }

    private fun completionSections(appointment: Appointment): List<InfoLine> {
        val completedBy = appointment.completedBy?.let { completer ->
            InfoLine(
                id = COMPLETED_BY_ID,
                title = AppointmentsRes.strings.appointments_details_completed_by.desc(),
                value = completer.label()
            )
        }
        return listOfNotNull(completedBy) + appointment.priceAdjustment?.sections().orEmpty()
    }

    private fun PriceAdjustment.sections(): List<InfoLine> {
        return listOfNotNull(
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_final_price,
                value = price.toString()
            ),
            InfoLine(
                title = AppointmentsRes.strings.appointments_details_additional_services,
                value = additionalServices.joinToString { it.name }
            ).takeIf { additionalServices.isNotEmpty() },
            reason?.takeIf { it.isNotBlank() }?.let {
                InfoLine(
                    title = AppointmentsRes.strings.appointments_details_price_adjustment_reason,
                    value = it
                )
            }
        )
    }

    private fun AppointmentCompletedBy.label(): StringDesc {
        return when (this) {
            AppointmentCompletedBy.SYSTEM -> AppointmentsRes.strings.appointments_details_completed_by_system.desc()
            AppointmentCompletedBy.USER -> AppointmentsRes.strings.appointments_details_completed_by_user.desc()
        }
    }

    private companion object {
        const val COMPLETED_BY_ID = "appointment_completed_by"
    }
}
