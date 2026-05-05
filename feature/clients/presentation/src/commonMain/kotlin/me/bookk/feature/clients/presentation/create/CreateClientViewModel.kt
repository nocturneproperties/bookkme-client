package me.bookk.feature.clients.presentation.create

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.ClientsStateFactory
import kotlin.uuid.Uuid

class CreateClientViewModel(
    private val businessId: Uuid,
    private val createClient: CreateClient,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: CreateClientState = stateFactory.createClientState().setup()

    private fun onNameChanged(text: String) {
        uiState.name.text = text
        uiState.name.isValid = text.length > 1
        invalidateButton()
    }

    private fun onLastNameChanged(text: String) {
        uiState.lastName.text = text
        uiState.lastName.isValid = text.length > 1
        invalidateButton()
    }

    private fun onPhoneChanged(text: String) {
        uiState.phone.text = text
        uiState.phone.isValid = text.length > 4
        invalidateButton()
    }

    private fun onSubmit() {
        val client = Client.Detached(
            id = Uuid.random(),
            name = uiState.name.text,
            lastName = uiState.lastName.text,
            phone = uiState.phone.text,
            businessId = businessId
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.submit.startLoading() },
            call = { createClient(client) },
            onComplete = { uiState.navigation.push(CreateClientDestination.Details(it.id)) },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = { uiState.submit.stopLoading() }
        )
    }

    private fun invalidateButton() {
        uiState.submit.isEnabled = uiState.name.isValid &&
                uiState.lastName.isValid &&
                uiState.phone.isValid
    }

    private fun CreateClientState.setup(): CreateClientState {
        appBar.title = ClientsRes.strings.clients_create_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(CreateClientDestination.Back)
        }

        name.label = ClientsRes.strings.clients_create_name.desc()
        name.placeholder = ClientsRes.strings.clients_create_name_placeholder.desc()
        name.inputType = InputType.TEXT
        name.onTextChanged = weakSelfClosure { vm, text -> vm.onNameChanged(text) }
        lastName.label = ClientsRes.strings.clients_create_last_name.desc()
        lastName.placeholder = ClientsRes.strings.clients_create_last_name_placeholder.desc()
        lastName.inputType = InputType.TEXT
        lastName.onTextChanged = weakSelfClosure { vm, text -> vm.onLastNameChanged(text) }
        phone.label = ClientsRes.strings.clients_create_phone.desc()
        phone.placeholder = ClientsRes.strings.clients_create_phone_placeholder.desc()
        phone.inputType = InputType.PHONE
        phone.onTextChanged = weakSelfClosure { vm, text -> vm.onPhoneChanged(text) }

        submit.isEnabled = false
        submit.text = DesignSystem.strings.action_create.desc()
        submit.onClick = weakSelfClosure { it.onSubmit() }
        return this
    }

}