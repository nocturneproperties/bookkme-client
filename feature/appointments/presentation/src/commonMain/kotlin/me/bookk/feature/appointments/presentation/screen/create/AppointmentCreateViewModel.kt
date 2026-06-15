package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentCreateViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentOptions: GetAppointmentOptions,
    private val dateLocalizer: DateLocalizer,
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
        uiState.datePicker.textField.text = dateFormat.format(date)
        uiState.datePicker.pickedDate = date
        invalidateButton()
    }

    private fun onTimePicked(date: LocalTime) {
        uiState.timePicker.textField.text = dateFormat.format(date)
        uiState.timePicker.pickedTime = date
        invalidateButton()
    }

    private fun invalidateButton() {
        uiState.create.isEnabled = uiState.clientPicker.selectedItem != null &&
                uiState.servicePicker.selectedItems.isNotEmpty() &&
                uiState.datePicker.pickedDate != null &&
                uiState.timePicker.pickedTime != null
    }

    private fun onCreateClick() {

    }

    private fun AppointmentCreateState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.title = AppointmentsRes.strings.appointments_create_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(AppointmentCreateDestination.Back)
        }

        clientPicker.pickerTitle = AppointmentsRes.strings.appointments_create_client_placeholder.desc()
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
