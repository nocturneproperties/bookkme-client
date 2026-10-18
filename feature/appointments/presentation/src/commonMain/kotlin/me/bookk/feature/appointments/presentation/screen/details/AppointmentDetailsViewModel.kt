package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.LocalDateTime
import library.money.api.Money
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
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CancelAppointment.Error
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
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
    private val getAppointmentOptions: GetAppointmentOptions,
    private val observeAppointmentOptions: ObserveAppointmentOptions,
    private val dateLocalizer: DateLocalizer,
    private val device: DeviceFacade,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentDetailsState = stateFactory.createAppointmentDetailsState().setup()
    private var appointment = Appointment.stub(id = appointmentId)
    private var isObservingServiceOptions = false

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
        uiState.completion.isSheetVisible = true
        if (!isObservingServiceOptions) {
            isObservingServiceOptions = true
            observeServiceOptions()
            refreshServiceOptions()
        }
    }

    private fun observeServiceOptions() {
        observeAppointmentOptions()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderServiceOptions(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun refreshServiceOptions() {
        val businessId = appointment.businessId
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentOptions(businessId) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderServiceOptions(options: AppointmentOptions) {
        uiState.completion.servicePicker.replaceOptions(options.services.map(::ServicePickerPresentation))
    }

    private fun onCompletionServicesPicked(services: List<ServicePickerPresentation>) {
        val picker = uiState.completion.servicePicker
        picker.replaceSelected((picker.selectedItems + services).distinctBy { it.pickerItemId })
        renderCalculatedPrice()
    }

    private fun onCompletionServicesRemove(services: List<ServicePickerPresentation>) {
        val picker = uiState.completion.servicePicker
        val removedIds = services.map { it.pickerItemId }.toSet()
        picker.replaceSelected(picker.selectedItems.filterNot { it.pickerItemId in removedIds })
        renderCalculatedPrice()
    }

    private fun renderCalculatedPrice() {
        val provided = uiState.completion.servicePicker.selectedItems
        if (provided.isEmpty()) {
            onPriceChanged("")
        } else {
            onPriceChanged(calculatedPrice(provided).toPriceInput())
        }
    }

    private fun calculatedPrice(provided: List<ServicePickerPresentation>): Money {
        return provided.map { it.item }
            .ifEmpty { appointment.services }
            .map { it.price }
            .reduce { acc, money -> acc + money }
    }

    private fun onPriceChanged(text: String) {
        val price = uiState.completion.price
        price.text = text
        if (text.isBlank() || text.toPriceOrNull() != null) {
            price.clearError()
        } else {
            price.showError(AppointmentsRes.strings.appointments_complete_price_error.desc())
        }
        invalidateConfirmButton()
    }

    private fun onNoteChanged(text: String) {
        uiState.completion.note.text = text
        uiState.completion.note.clearError()
        invalidateConfirmButton()
    }

    private fun String.toPriceOrNull(): Money? {
        return toMoneyOrNull(appointment.currency())?.takeIf { it.value >= 0 }
    }

    private fun isCompletionInputValid(): Boolean {
        return uiState.completion.price.isValid && uiState.completion.note.isValid
    }

    private fun invalidateConfirmButton() {
        uiState.completion.confirmButton.isEnabled = isCompletionInputValid()
    }

    private fun priceAdjustmentDraft(): PriceAdjustmentDraft? {
        val provided = uiState.completion.servicePicker.selectedItems
        val priceText = uiState.completion.price.text
        val reason = uiState.completion.note.text.trim()
        if (provided.isEmpty() && priceText.isBlank() && reason.isEmpty()) return null
        return PriceAdjustmentDraft(
            additionalServiceIds = provided.map { it.item.id },
            price = priceText.toPriceOrNull() ?: calculatedPrice(provided),
            reason = reason.ifEmpty { null }
        )
    }

    private fun onConfirmCompletion() {
        val priceAdjustment = priceAdjustmentDraft()
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.completion.confirmButton.startLoading() },
            call = { completeAppointment(appointmentId, priceAdjustment) },
            onComplete = {
                uiState.completion.isSheetVisible = false
                renderAppointment(it)
            },
            onTerminate = { uiState.completion.confirmButton.stopLoading(enable = isCompletionInputValid()) },
            onError = ::onCompletionError
        )
    }

    private fun onCompletionError(error: Throwable) {
        when (error) {
            is CompleteAppointment.Error.NegativePrice -> uiState.completion.price.showError(
                AppointmentsRes.strings.appointments_complete_price_error.desc()
            )
            is CompleteAppointment.Error.CurrencyMismatch -> uiState.completion.price.showError(
                AppointmentsRes.strings.appointments_complete_currency_error.desc()
            )
            is CompleteAppointment.Error.ReasonTooLong -> uiState.completion.note.showError(
                AppointmentsRes.strings.appointments_complete_note_too_long_error.desc()
            )
            else -> {
                uiState.completion.isSheetVisible = false
                onCompletionRejected(error)
            }
        }
    }

    private fun onCompletionRejected(error: Throwable) {
        when (error) {
            is CompleteAppointment.Error.AppointmentAlreadyCancelled -> renderStatus(AppointmentStatus.CANCELLED)
            is CompleteAppointment.Error.AppointmentMarkedNoShow -> renderStatus(AppointmentStatus.NO_SHOW)
            is CompleteAppointment.Error.AppointmentNotStarted -> showMessage(
                AppointmentsRes.strings.appointments_details_not_started_error.desc()
            )
            is CompleteAppointment.Error.ServiceNotFound -> {
                showMessage(AppointmentsRes.strings.appointments_complete_service_not_found_error.desc())
                refreshServiceOptions()
            }
            else -> uiState.notifications.add(error.notification())
        }
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
        uiState.statusNote = statusNote(appointment)
        uiState.appBar.actions.replace(appBarActions(appointment))
        uiState.dateTimePicker.pickedDate = appointment.date
        uiState.dateTimePicker.onDatePicked = weakVMClosure { vm, v -> vm.onDatePicked(v) }
        uiState.rescheduleButton.isVisible = appointment.canBeRescheduled()
        uiState.completeButton.isVisible = appointment.canBeCompleted()
        uiState.noShowButton.isVisible = appointment.canBeMarkedNoShow()
        uiState.infoSections.replace(createSections(appointment))
        uiState.completion.price.placeholder = calculatedPrice(emptyList()).toPriceInput().desc()
        uiState.completion.price.suffix = appointment.services.first().price.currency.symbol().desc()
        renderServices(appointment)
        renderPriceAdjustment(appointment.priceAdjustment)
        renderTotal(appointment)
    }

    private fun Appointment.currency(): Money.SupportedCurrency {
        return services.first().price.currencyType
    }

    private fun renderServices(appointment: Appointment) {
        uiState.servicePicker.isVisible = true
        uiState.servicePicker.pickerTitle = if (!appointment.priceAdjustment?.additionalServices.isNullOrEmpty()) {
            AppointmentsRes.strings.appointments_details_initial_services.desc()
        } else {
            AppointmentsRes.strings.appointments_create_services.desc()
        }
        uiState.servicePicker.replaceSelected(appointment.services.pickerItems())
    }

    private fun renderPriceAdjustment(priceAdjustment: PriceAdjustment?) {
        val providedServices = priceAdjustment?.additionalServices.orEmpty()
        uiState.priceAdjustment.isVisible = providedServices.isNotEmpty()
        uiState.priceAdjustment.reason = priceAdjustment?.reason?.takeIf { it.isNotBlank() }?.desc()
        uiState.priceAdjustment.servicePicker.isVisible = providedServices.isNotEmpty()
        uiState.priceAdjustment.servicePicker.replaceSelected(providedServices.pickerItems())
    }

    private fun renderTotal(appointment: Appointment) {
        val bookedPrice = calculatedPrice(emptyList())
        val finalPrice = appointment.priceAdjustment?.price ?: bookedPrice
        uiState.totalPrice = finalPrice.toString()
        uiState.originalPrice = bookedPrice.toString().takeIf { finalPrice != bookedPrice }
    }

    private fun List<ServiceSnapshot>.pickerItems(): List<ServicePickerPresentation> {
        return mapIndexed { index, service ->
            ServicePickerPresentation(service).copy(pickerItemId = "${service.id}_$index")
        }
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
        totalLabel = AppointmentsRes.strings.appointments_details_total.desc()

        priceAdjustment.reasonTitle = AppointmentsRes.strings.appointments_details_price_adjustment_reason.desc()
        priceAdjustment.servicePicker.isEditable = false
        priceAdjustment.servicePicker.isVisible = false
        priceAdjustment.servicePicker.pickerTitle = AppointmentsRes.strings.appointments_details_adjusted_services.desc()

        completion.setup()
    }

    private fun AppointmentCompletionState.setup() {
        title = AppointmentsRes.strings.appointments_complete_title.desc()

        servicePicker.pickerTitle = AppointmentsRes.strings.appointments_create_services.desc()
        servicePicker.pickerType = PickerFieldState.PickerType.SCREEN
        servicePicker.addItemText = AppointmentsRes.strings.appointments_create_services_add.desc()
        servicePicker.placeholder = AppointmentsRes.strings.appointments_create_services_empty.desc()
        servicePicker.onItemsPicked = weakVMClosure { vm, items -> vm.onCompletionServicesPicked(items) }
        servicePicker.onItemsRemoveRequested = weakVMClosure { vm, items -> vm.onCompletionServicesRemove(items) }
        servicesHint = AppointmentsRes.strings.appointments_complete_services_hint.desc()

        price.label = AppointmentsRes.strings.appointments_details_final_price.desc()
        price.inputType = InputType.DECIMAL
        price.onTextChanged = weakVMClosure { vm, text -> vm.onPriceChanged(text) }

        note.placeholder = AppointmentsRes.strings.appointments_complete_note.desc()
        note.onTextChanged = weakVMClosure { vm, text -> vm.onNoteChanged(text) }

        confirmButton.text = AppointmentsRes.strings.appointments_details_complete.desc()
        confirmButton.onClick = weakVMClosure { it.onConfirmCompletion() }
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
        )
    }

    private fun statusNote(appointment: Appointment): StringDesc? {
        if (appointment.completedBy != AppointmentCompletedBy.USER) return null
        return AppointmentsRes.strings.appointments_details_marked_manually.desc()
    }
}
