package me.bookk.feature.employees.presentation.screen.invite

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class InviteEmployeeViewModel(
    @InjectedParam private val businessId: Uuid,
    stateFactory: EmployeesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: InviteEmployeeState = stateFactory.createInviteEmployeeState().setup()

    private fun InviteEmployeeState.setup() = apply {
        appBar.title = EmployeesRes.strings.employees_invite_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(InviteEmployeeDestinations.Back) }
    }
}
