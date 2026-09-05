package me.bookk.feature.clients.presentation.edit

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import library.validation.api.ValidateEmail
import library.validation.api.ValidateEmail.Result.Invalid.Format.isValid
import library.validation.api.ValidateName
import library.validation.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.EditClient
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.edit.EditClientDestination.Back
import me.bookk.feature.clients.presentation.edit.EditClientDestination.Deleted
import kotlin.properties.Delegates.notNull
import kotlin.uuid.Uuid

class EditClientViewModel(
    private val id: Uuid,
    private val getClient: GetClient,
    private val editClient: EditClient,
    private val deleteClient: DeleteClient,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EditClientState = stateFactory.createEditClientState().setup()
    private var client: Client by notNull()

    init {
        loadClient()
    }

    private fun loadClient() {
        launch(
            launchIn = Dispatchers.Default,
            call = { getClient(id) },
            onComplete = {
                client = it
                uiState.isAttachedInfoVisible = it is Client.Integrated
                uiState.name.enabled = it is Client.Detached
                uiState.lastName.enabled = it is Client.Detached
                uiState.phone.enabled = it is Client.Detached
                uiState.email.enabled = it is Client.Detached
                uiState.name.text = it.name
                uiState.name.isValid = validateName(it.name).isValid
                uiState.lastName.text = it.lastName
                uiState.lastName.isValid = it.lastName.length > 1
                uiState.phone.text = it.phone.orEmpty()
                uiState.phone.isValid = it.phone.orEmpty().length > 4
                uiState.email.text = it.email.orEmpty()
                uiState.email.isValid = it.email.orEmpty().isEmpty() || validateEmail(it.email.orEmpty()).isValid
                uiState.description.text = it.description.orEmpty()
                invalidateButton()
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun onNameChanged(text: String) {
        uiState.name.text = text
        uiState.name.isValid = validateName(text).isValid
        invalidateButton()
    }

    private fun onLastNameChanged(text: String) {
        uiState.lastName.text = text
        uiState.lastName.isValid = text.length > 1
        invalidateButton()
    }

    private fun onPhoneChanged(text: String) {
        uiState.phone.text = text.filter { it.isDigit() || it == '+' }
        uiState.phone.isValid = text.length > 4
        invalidateButton()
    }

    private fun onEmailChanged(text: String) {
        uiState.email.text = text
        uiState.email.isValid = text.isEmpty() || validateEmail(text).isValid
        invalidateButton()
    }

    private fun onDescriptionChanged(text: String) {
        uiState.description.text = text
    }

    private fun onSubmit() {
        val edited = when (val current = client) {
            is Client.Detached -> current.copy(
                name = uiState.name.text,
                lastName = uiState.lastName.text,
                phone = uiState.phone.text,
                email = uiState.email.text,
                description = uiState.description.text
            )

            is Client.Integrated -> current.copy(
                description = uiState.description.text
            )
        }
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.submit.startLoading() },
            call = { editClient(edited) },
            onComplete = { uiState.navigation.push(Back) },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = { uiState.submit.stopLoading() }
        )
    }

    private fun onDeleteClicked() {
        uiState.notifications.add(
            PresentationNotification.Message.deleteConfirmation(
                message = ClientsRes.strings.clients_delete_desc.desc(),
                onConfirmed = ::onDeleteConfirmed
            )
        )
    }

    private fun onDeleteConfirmed() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.deleteButton.startLoading() },
            call = { deleteClient(client) },
            onComplete = { uiState.navigation.push(Deleted) },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = { uiState.deleteButton.stopLoading() }
        )
    }

    private fun invalidateButton() {
        uiState.submit.isEnabled = uiState.name.isValid &&
                uiState.lastName.isValid &&
                uiState.phone.isValid &&
                uiState.email.isValid
    }

    private fun EditClientState.setup(): EditClientState {
        appBar.size = TopBarSize.SMALL
        appBar.title = ClientsRes.strings.clients_edit_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }

        attachedInfoText = ClientsRes.strings.clients_edit_attached_info.desc()
        isAttachedInfoVisible = false

        name.isValid = false
        name.label = ClientsRes.strings.clients_create_name.desc()
        name.placeholder = ClientsRes.strings.clients_create_name_placeholder.desc()
        name.inputType = InputType.TEXT
        name.onTextChanged = weakVMClosure { vm, text -> vm.onNameChanged(text) }
        lastName.isValid = false
        lastName.label = ClientsRes.strings.clients_create_last_name.desc()
        lastName.placeholder = ClientsRes.strings.clients_create_last_name_placeholder.desc()
        lastName.inputType = InputType.TEXT
        lastName.onTextChanged = weakVMClosure { vm, text -> vm.onLastNameChanged(text) }
        phone.isValid = false
        phone.label = ClientsRes.strings.clients_create_phone.desc()
        phone.placeholder = ClientsRes.strings.clients_create_phone_placeholder.desc()
        phone.inputType = InputType.PHONE
        phone.onTextChanged = weakVMClosure { vm, text -> vm.onPhoneChanged(text) }
        email.isValid = true
        email.label = ClientsRes.strings.clients_create_email.desc()
        email.placeholder = ClientsRes.strings.clients_create_email_placeholder.desc()
        email.inputType = InputType.EMAIL
        email.onTextChanged = weakVMClosure { vm, text -> vm.onEmailChanged(text) }

        description.isValid = true
        description.placeholder = ClientsRes.strings.clients_edit_description_placeholder.desc()
        description.inputType = InputType.TEXT
        description.onTextChanged = weakVMClosure { vm, text -> vm.onDescriptionChanged(text) }

        submit.isEnabled = false
        submit.text = DesignSystem.strings.action_save.desc()
        submit.onClick = weakVMClosure { it.onSubmit() }

        deleteButton.text = ClientsRes.strings.clients_delete_button.desc()
        deleteButton.onClick = weakVMClosure { it.onDeleteClicked() }
        return this
    }
}
