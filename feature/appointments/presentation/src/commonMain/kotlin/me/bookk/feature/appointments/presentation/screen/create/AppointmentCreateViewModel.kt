package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentCreateViewModel(
    @InjectedParam private val businessId: Uuid,
    private val stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentCreateState = stateFactory.createAppointmentCreateState().setup()

    private fun onClientSelected(client: SimplePickerPresentation<ClientSnapshot>?) {
        uiState.clientPicker.selectedItem = client
    }

    private fun onDatePicked(date: LocalDate) {
        uiState.datePicker.pickedDate = date
    }

    private fun onTimePicked(date: LocalTime) {
        uiState.timePicker.pickedTime = date
    }

    private fun onCreateClick() {

    }

    private fun AppointmentCreateState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.title = AppointmentsRes.strings.appointments_create_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(AppointmentCreateDestination.Back)
        }

        clientPicker.textField.label = AppointmentsRes.strings.appointments_create_client.desc()
        clientPicker.textField.placeholder = AppointmentsRes.strings.appointments_create_client_placeholder.desc()
        clientPicker.onItemPicked = weakSelfClosure { vm, item -> vm.onClientSelected(item) }

        servicePicker.pickerTitle = AppointmentsRes.strings.appointments_create_services.desc()
        servicePicker.addItemText = AppointmentsRes.strings.appointments_create_services_add.desc()
        servicePicker.onItemsPicked = weakSelfClosure { vm, items -> vm.uiState.servicePicker.replaceSelected(items) }
        servicePicker.onItemsRemoveRequested = weakSelfClosure { vm, items ->
            val list = vm.uiState.servicePicker.selectedItems.toSet()
            vm.uiState.servicePicker.replaceSelected(list.minus(items.toSet()).toList())
        }
        servicePicker.replaceOptions(listOf(ServicePickerPresentation.stub()))

        datePicker.textField.label = AppointmentsRes.strings.appointments_create_date.desc()
        datePicker.textField.placeholder = AppointmentsRes.strings.appointments_create_date_placeholder.desc()
        datePicker.onDatePicked = weakSelfClosure { vm, date -> vm.onDatePicked(date) }

        timePicker.textField.label = AppointmentsRes.strings.appointments_create_time.desc()
        timePicker.textField.placeholder = AppointmentsRes.strings.appointments_create_time_placeholder.desc()
        timePicker.onTimePicked = weakSelfClosure { vm, time -> vm.onTimePicked(time) }

        note.placeholder = AppointmentsRes.strings.appointments_create_note.desc()

        create.text = DesignSystem.strings.action_create.desc()
        create.onClick = weakSelfClosure { it.onCreateClick() }
    }
}
