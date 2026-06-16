package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentCreateViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentOptions: GetAppointmentOptions,
    private val createAppointment: CreateAppointment,
    dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentCreateState = stateFactory.createAppointmentCreateState().setup()
    private val dateFormat = dateLocalizer.forStyle(DateStyle.SHORT)

    init {
        loadAppointmentParameters()
    }

    private fun loadAppointmentParameters() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentOptions(businessId) },
            onComplete = {
                uiState.clientPicker.replaceOptions(it.clients.map(ClientSnapshot::pickerItem))
                uiState.servicePicker.replaceOptions(it.services.map(::ServicePickerPresentation))
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onClientSelected(client: SimplePickerPresentation<ClientSnapshot>?) {
        uiState.clientPicker.selectedItem = client
        uiState.clientPicker.textField.text = client?.domain?.fullName.orEmpty()
        invalidateButton()
    }

    private fun onServicesPicked(services: List<ServicePickerPresentation>) {
        val list = uiState.servicePicker.selectedItems.toSet()
        uiState.servicePicker.replaceSelected(list.plus(services.toSet()).toList())
        updateSubtotal()
        invalidateButton()
    }

    private fun onServicesRemove(services: List<ServicePickerPresentation>) {
        val list = uiState.servicePicker.selectedItems.toSet()
        uiState.servicePicker.replaceSelected(list.minus(services.toSet()).toList())
        updateSubtotal()
        invalidateButton()
    }

    private fun updateSubtotal() {
        val items = uiState.servicePicker.selectedItems
        if (items.isEmpty()) {
            uiState.subtotalLabel = "".desc()
            uiState.subtotalPrice = ""
        } else {
            val total = items.map { it.item.price }.reduce { acc, money -> acc + money }
            uiState.subtotalLabel =
                AppointmentsRes.strings.appointments_create_subtotal.format(items.size)
            uiState.subtotalPrice = total.toString()
        }
    }

    private fun onDatePicked(date: LocalDate) {
        uiState.timePicker.textField.clearError()
        uiState.datePicker.textField.clearError()
        uiState.datePicker.textField.text = dateFormat.format(date)
        uiState.datePicker.pickedDate = date
        invalidateButton()
    }

    private fun onTimePicked(date: LocalTime) {
        uiState.timePicker.textField.clearError()
        uiState.datePicker.textField.clearError()
        uiState.timePicker.textField.text = dateFormat.format(date)
        uiState.timePicker.pickedTime = date
        invalidateButton()
    }

    private fun invalidateButton() {
        uiState.create.isEnabled = uiState.clientPicker.selectedItem != null &&
                uiState.servicePicker.selectedItems.isNotEmpty() &&
                uiState.datePicker.pickedDate != null &&
                uiState.timePicker.pickedTime != null &&
                uiState.datePicker.textField.isValid &&
                uiState.timePicker.textField.isValid
    }

    private fun onCreateClick() {
        val client = uiState.clientPicker.selectedItem?.domain ?: return
        val services = uiState.servicePicker.selectedItems
        val date = uiState.datePicker.pickedDate ?: return
        val time = uiState.timePicker.pickedTime ?: return

        val draft = AppointmentDraft(
            businessId = businessId,
            client = client,
            services = services.map { it.item },
            date = LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault()),
            note = uiState.note.text
        )

        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.create.startLoading() },
            call = { createAppointment(draft) },
            onComplete = { uiState.navigation.push(AppointmentCreateDestination.Back) },
            onError = {
                when (it) {
                    is CreateAppointment.Error.AppointmentOverlap -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                            AppointmentsRes.strings.appointments_create_overlap_error.desc()
                            )
                        )
                    }
                    is CreateAppointment.Error.DateIsNotAllowed -> {
                        uiState.datePicker.textField.showError(AppointmentsRes.strings.appointments_create_date_error.desc())
                        uiState.create.isEnabled = false
                    }
                    is CreateAppointment.Error.TimeIsNotAllowed -> {
                        uiState.datePicker.textField.showError(AppointmentsRes.strings.appointments_create_time_error.desc())
                        uiState.create.isEnabled = false
                    }
                    else -> uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { uiState.create.stopLoading() }
        )
    }

    private fun AppointmentCreateState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.title = AppointmentsRes.strings.appointments_create_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(AppointmentCreateDestination.Back)
        }

        clientPicker.pickerTitle =
            AppointmentsRes.strings.appointments_create_client_placeholder.desc()
        clientPicker.pickerType = PickerFieldState.PickerType.SCREEN
        clientPicker.textField.label = AppointmentsRes.strings.appointments_create_client.desc()
        clientPicker.textField.placeholder =
            AppointmentsRes.strings.appointments_create_client_placeholder.desc()
        clientPicker.onItemPicked = weakSelfClosure { vm, item -> vm.onClientSelected(item) }

        servicePicker.pickerTitle = AppointmentsRes.strings.appointments_create_services.desc()
        servicePicker.addItemText = AppointmentsRes.strings.appointments_create_services_add.desc()
        servicePicker.onItemsPicked = weakSelfClosure { vm, items ->
            vm.onServicesPicked(items)
        }
        servicePicker.onItemsRemoveRequested = weakSelfClosure { vm, items ->
            vm.onServicesRemove(items)
        }

        datePicker.textField.label = AppointmentsRes.strings.appointments_create_date.desc()
        datePicker.textField.placeholder =
            AppointmentsRes.strings.appointments_create_date_placeholder.desc()
        datePicker.onDatePicked = weakSelfClosure { vm, date -> vm.onDatePicked(date) }

        timePicker.textField.label = AppointmentsRes.strings.appointments_create_time.desc()
        timePicker.textField.placeholder =
            AppointmentsRes.strings.appointments_create_time_placeholder.desc()
        timePicker.onTimePicked = weakSelfClosure { vm, time -> vm.onTimePicked(time) }

        note.placeholder = AppointmentsRes.strings.appointments_create_note.desc()

        create.text = DesignSystem.strings.action_create.desc()
        create.isEnabled = false
        create.onClick = weakSelfClosure { it.onCreateClick() }
    }
}
