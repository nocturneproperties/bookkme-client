package me.bookk.feature.services.presentation.group.add

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.presentation.ServicesStateFactory
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AddGroupViewModel(
    private val createGroup: CreateServiceGroup,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AddGroupState = stateFactory.createAddGroupState().setup()

    private fun onNameChanged(text: String) {
        uiState.name.text = text
        uiState.create.isEnabled = text.length > 1
        uiState.name.validationState = ValidationState.DEFAULT
        uiState.name.supportingTextRes = null
    }

    private fun onCreate() {
        val name = uiState.name.text
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.create.startLoading() },
            call = {
                val businessId = observeCurrentBusinessId().filterNotNull().first()
                val group = ServiceGroup(
                    id = Uuid.random(),
                    businessId = businessId,
                    name = name,
                    createdAt = Clock.System.now()
                )
                createGroup(group)
            },
            onComplete = { uiState.navigation.push(AddGroupNavigation.Dismiss) },
            onError = {
                when (it) {
                    is CreateServiceGroup.Error.InvalidName -> {
                        uiState.name.validationState = ValidationState.ERROR
                        uiState.name.supportingTextRes = ServicesRes.strings.services_group_add_name_invalid.desc()
                    }
                    is CreateServiceGroup.Error.NameExists -> {
                        uiState.name.validationState = ValidationState.ERROR
                        uiState.name.supportingTextRes = ServicesRes.strings.services_group_add_name_exists.desc()
                    }
                    else -> uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { uiState.create.stopLoading() }
        )
    }

    private fun AddGroupState.setup() = apply {
        title = ServicesRes.strings.services_group_add_title.desc()

        name.placeholder = ServicesRes.strings.services_create_name.desc()
        name.onTextChanged = weakVMClosure { vm, text -> vm.onNameChanged(text) }
        name.maxLength = 512

        create.isEnabled = false
        create.text = DesignSystem.strings.action_create.desc()
        create.onClick = weakVMClosure { it.onCreate() }
    }
}