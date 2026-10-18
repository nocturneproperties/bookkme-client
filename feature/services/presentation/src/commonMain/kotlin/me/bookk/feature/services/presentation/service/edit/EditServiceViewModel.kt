package me.bookk.feature.services.presentation.service.edit

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.services.presentation.service.edit.EditServiceDestination.Back
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class EditServiceViewModel(
    @InjectedParam private val serviceId: Uuid,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EditServiceState = stateFactory.createEditServiceState().setup()

    private fun EditServiceState.setup() = apply {
        appBar.title = ServicesRes.strings.services_edit_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(Back)
        }
    }
}
