package me.bookk.feature.appointments.presentation.screen.request

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentRequestViewModel(
    @InjectedParam private val businessId: Uuid,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentRequestState = stateFactory.createAppointmentRequestState().setup()

    private fun AppointmentRequestState.setup() = apply {
        appBar.title = AppointmentsRes.strings.appointments_request_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(AppointmentRequestDestinations.Back) }
    }
}
