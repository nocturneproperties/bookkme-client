package me.bookk.feature.clients.presentation.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.Logger
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.core.presentation.text.TextCollator
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.GetDeviceContacts
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.list.ClientsListDestination.AddClient
import kotlin.uuid.Uuid

class ClientsListViewModel(
    private val getClientsList: GetClientsList,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val getClientsPermissions: GetClientsPermissions,
    private val getDeviceContacts: GetDeviceContacts,
    private val textCollator: TextCollator,
    private val permissionManager: PermissionManager,
    private val device: DeviceFacade,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createClientsListState().setup()
    private var items = listOf<ClientSection>()

    init {
        observeClients()
        observeBusinessChanges()
    }

    private fun observeClients() {
        getClientsList.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderClients(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderClients(clients: List<Client>) {
        if (clients.isEmpty() && uiState.clientsList.isInitialLoading) return
        val grouped = clients
            .sortedBy { it.name.trim() }
            .groupBy { it.name[0].toString().capitalizeChar() }
            .map { entry ->
                ClientSection(
                    id = entry.key,
                    header = entry.key,
                    items = entry.value,
                    onItemClick = weakVMClosure { vm, client -> vm.onClientClick(client) }
                )
            }
        items = grouped
        uiState.clientsList.replace(grouped)
    }

    private fun observeBusinessChanges() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.clientsList)
            .safeOnEach {
                loadClients(it)
                loadEditAccess(it)
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadEditAccess(businessId: Uuid) {
        launch(
            key = EDIT_ACCESS_KEY,
            launchIn = DispatcherProvider.io,
            call = { getClientsPermissions(businessId) },
            onComplete = { renderAddAction(it.canEdit) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderAddAction(canEdit: Boolean) {
        val actions = if (canEdit) {
            listOf(
                AppBarAction(
                    icon = DesignSystem.images.contacts,
                    contentDescription = DesignSystem.strings.action_import.desc(),
                    onClick = weakVMClosure { it.onImportClientsClick() }
                ),
                AppBarAction(
                    icon = DesignSystem.images.plus,
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.onAddClientClick() }
                )
            )
        } else {
            emptyList()
        }
        uiState.appBar.actions.replace(actions)
    }

    private fun loadClients(businessId: Uuid) {
        loadList(
            listState = uiState.clientsList,
            notifications = uiState.notifications,
            refreshState = uiState.refreshState,
            call = { getClientsList.refresh(businessId) }
        )
    }

    private fun onClientClick(client: Client) {
        uiState.navigation.push(ClientsListDestination.ClientDetails(client.id))
    }

    private fun onImportClientsClick() {
        launch(
            launchIn = DispatcherProvider.main,
            call = { permissionManager.requestPermission(PermissionType.CONTACTS) },
            onComplete = { isGranted ->
                if (isGranted)
                    readDeviceContacts()
                else
                    showContactsAccessDialog()
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun readDeviceContacts() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getDeviceContacts().toContactOptions() },
            onComplete = { showContactsPicker(it) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun List<ClientImportEntry>.toContactOptions(): List<SimplePickerPresentation<ClientImportEntry>> {
        return map { contact -> contact.pickerName() to contact }
            .sortedWith(compareBy(textCollator.comparator()) { (name, _) -> name })
            .mapIndexed { index, (name, contact) ->
                SimplePickerPresentation(
                    pickerItemId = index.toString(),
                    displayName = name.desc(),
                    domain = contact
                )
            }
    }

    private fun showContactsPicker(options: List<SimplePickerPresentation<ClientImportEntry>>) {
        if (options.isEmpty()) {
            showNoContactsMessage()
            return
        }
        uiState.contactsPicker.replaceOptions(options)
        uiState.isContactsPickerVisible = true
    }

    private fun showNoContactsMessage() {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = ClientsRes.strings.clients_import_no_contacts_title.desc(),
                message = ClientsRes.strings.clients_import_no_contacts_message.desc(),
                buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
            )
        )
    }

    private fun ClientImportEntry.pickerName(): String {
        return "$name $lastName".trim().ifBlank { phone ?: email.orEmpty() }
    }

    private fun onContactsPicked() {
        uiState.isContactsPickerVisible = false
    }

    private fun showContactsAccessDialog() {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = ClientsRes.strings.clients_import_permission_title.desc(),
                message = ClientsRes.strings.clients_import_permission_message.desc(),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                        actionType = ActionType.CANCEL
                    ),
                    ButtonDescriptor(
                        text = ClientsRes.strings.clients_import_permission_open_settings.desc(),
                        actionType = ActionType.POSITIVE,
                        onClick = weakVMClosure { it.device.openAppSettings() }
                    )
                )
            )
        )
    }

    private fun onAddClientClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(AddClient(it)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) {
            uiState.clientsList.replace(items)
            return
        }
        val filtered = items
            .map {
                it.copy(items = it.items.filter {
                    it.fullName.contains(query, ignoreCase = true)
                })
            }
            .filter { it.items.isNotEmpty() }
        uiState.clientsList.replace(filtered)
    }

    private fun ClientsListState.setup(): ClientsListState {
        appBar.title = ClientsRes.strings.clients_title.desc()
        appBar.onBackClick =
            weakVMClosure { it.uiState.navigation.push(ClientsListDestination.Back) }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        contactsPicker.pickerTitle = ClientsRes.strings.clients_import_contacts_title.desc()
        contactsPicker.pickerType = PickerFieldState.PickerType.SCREEN
        contactsPicker.onItemsPicked = weakVMClosure { vm, _ -> vm.onContactsPicked() }
        clientsList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ClientsRes.strings.clients_empty.desc()
        )
        return this
    }

    private companion object {
        const val EDIT_ACCESS_KEY = "clients_list_edit_access"
    }
}
