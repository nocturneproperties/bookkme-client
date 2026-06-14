package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
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

    private fun AppointmentCreateState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.title = AppointmentsRes.strings.appointments_create_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(AppointmentCreateDestination.Back)
        }

        clientPicker.textField.label = AppointmentsRes.strings.appointments_create_client.desc()
        clientPicker.onItemPicked = weakSelfClosure { vm, item -> vm.onClientSelected(item) }
        val initialItem = stateFactory.createServicePickerItemState().apply {
            textField.label = AppointmentsRes.strings.appointments_create_service.desc()
            onItemPicked = weakSelfClosure { vm, item -> this.selectedItem = item }
        }
        servicePickers.replace(listOf(initialItem))

        datePicker.textField.label = AppointmentsRes.strings.appointments_create_date.desc()
        datePicker.onDatePicked = weakSelfClosure { vm, date -> vm.onDatePicked(date) }

        timePicker.textField.label = AppointmentsRes.strings.appointments_create_date.desc()
        timePicker.onTimePicked = weakSelfClosure { vm, time -> vm.onTimePicked(time) }
    }
}
