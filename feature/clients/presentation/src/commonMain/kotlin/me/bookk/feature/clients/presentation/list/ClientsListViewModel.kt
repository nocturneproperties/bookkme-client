package me.bookk.feature.clients.presentation.list

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.joinToStringDesc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import library.device.api.DeviceFacade
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.core.presentation.text.TextCollator
import me.bookk.designsystem.convenience.listPager
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.action
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.GetDeviceContacts
import me.bookk.feature.clients.domain.api.ImportClients
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import me.bookk.feature.clients.domain.api.entity.ClientsQuery
import me.bookk.feature.clients.domain.api.entity.RejectedClientImport
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.list.ClientsListDestination.AddClient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

class ClientsListViewModel(
    private val getClientsList: GetClientsList,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val getClientsPermissions: GetClientsPermissions,
    private val getDeviceContacts: GetDeviceContacts,
    private val textCollator: TextCollator,
    private val importClients: ImportClients,
    private val permissionManager: PermissionManager,
    private val device: DeviceFacade,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createClientsListState().setup()
    private val searchText = MutableStateFlow("")
    private val clientPages = listPager(
        listState = uiState.clientsList,
        notifications = uiState.notifications,
        source = getClientsList,
        refreshState = uiState.refreshState,
    )
    private val importAction = uiState.appBar.action(
        icon = DesignSystem.images.contacts,
        contentDescription = DesignSystem.strings.action_import.desc(),
        onClick = weakVMClosure { it.onImportClientsClick() },
    )
    private val addAction = uiState.appBar.action(
        icon = DesignSystem.images.plus,
        contentDescription = DesignSystem.strings.action_add.desc(),
        onClick = weakVMClosure { it.onAddClientClick() },
    )

    init {
        observeClients()
        observeEditAccess()
        loadClients()
    }

    private fun observeClients() {
        searchText
            .flatMapLatest { getClientsList.flow(it) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderClients(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderClients(clients: List<Client>) {
        if (clients.isEmpty() && uiState.clientsList.isInitialLoading) return
        uiState.clientsList.replace(clients.map(::clientListItem))
    }

    private fun clientListItem(client: Client): ClientListItem {
        return ClientListItem(
            id = client.id.toString(),
            fullName = client.fullName,
            onClick = weakVMClosure { it.onClientClick(client) },
        )
    }

    private fun observeEditAccess() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { loadEditAccess(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadClients() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.clientsList)
            .combine(serverSearch()) { businessId, search -> ClientsQuery(businessId, search) }
            .distinctUntilChanged()
            .safeOnEach { clientPages.load(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun serverSearch(): Flow<String?> {
        return searchText
            .map { text -> text.trim().takeIf { it.length >= MIN_SERVER_SEARCH_LENGTH } }
            .debounce { if (it == null) Duration.ZERO else SERVER_SEARCH_DELAY }
            .distinctUntilChanged()
    }

    private fun loadEditAccess(businessId: Uuid) {
        launch(
            key = EDIT_ACCESS_KEY,
            launchIn = DispatcherProvider.io,
            call = { getClientsPermissions(businessId) },
            onComplete = { renderAppBarActions(it.canEdit) },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun renderAppBarActions(canEdit: Boolean) {
        val actions = if (canEdit) listOf(importAction, addAction) else emptyList()
        uiState.appBar.actions.replace(actions)
    }

    private fun onClientClick(client: Client) {
        uiState.navigation.push(ClientsListDestination.ClientDetails(client.id))
    }

    private fun onImportClientsClick() {
        launch(
            launchIn = DispatcherProvider.main,
            call = { permissionManager.requestPermission(PermissionType.CONTACTS) },
            onComplete = { isGranted ->
                if (isGranted) readDeviceContacts() else showContactsAccessDialog()
            },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun readDeviceContacts() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { importAction.startLoading() },
            call = { getDeviceContacts().toContactOptions() },
            onComplete = { showContactsPicker(it) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { importAction.stopLoading() },
        )
    }

    private fun List<ClientImportEntry>.toContactOptions():
        List<SimplePickerPresentation<ClientImportEntry>> {
        return map { contact -> contact.pickerName() to contact }
            .sortedWith(compareBy(textCollator.comparator()) { (name, _) -> name })
            .mapIndexed { index, (name, contact) ->
                SimplePickerPresentation(
                    pickerItemId = index.toString(),
                    displayName = name.desc(),
                    domain = contact,
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
                buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc())),
            ),
        )
    }

    private fun ClientImportEntry.pickerName(): String {
        return "$name $lastName".trim().ifBlank { phone ?: email.orEmpty() }
    }

    private fun onContactsPicked(picked: List<SimplePickerPresentation<ClientImportEntry>>) {
        uiState.isContactsPickerVisible = false
        importContacts(picked.map { it.domain })
    }

    private fun importContacts(contacts: List<ClientImportEntry>) {
        launch(
            key = IMPORT_CONTACTS_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { importAction.startLoading() },
            call = {
                val businessId = observeCurrentBusinessId().filterNotNull().first()
                importClients(businessId, contacts)
            },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                showImportSummary(it)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { importAction.stopLoading() },
        )
    }

    private fun showImportSummary(result: ClientImportResult) {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = ClientsRes.strings.clients_import_result_title.desc(),
                message = result.summaryLines().joinToStringDesc(separator = "\n"),
                buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc())),
            ),
        )
    }

    private fun ClientImportResult.summaryLines(): List<StringDesc> {
        val createdLine = ClientsRes.strings.clients_import_result_created.format(created.size)
        if (rejected.isEmpty()) return listOf(createdLine)
        val countsByReason = rejected.groupingBy { it.reason }.eachCount()
        val reasonLines = RejectedClientImport.Reason.entries.mapNotNull { reason ->
            countsByReason[reason]?.let { reason.summaryLine().format(it) }
        }
        return listOf(
            createdLine,
            ClientsRes.strings.clients_import_result_rejected.format(rejected.size),
        ) + reasonLines
    }

    private fun RejectedClientImport.Reason.summaryLine(): StringResource {
        return when (this) {
            RejectedClientImport.Reason.INVALID_FIELDS -> {
                ClientsRes.strings.clients_import_result_reason_invalid
            }

            RejectedClientImport.Reason.MISSING_CONTACT_INFO -> {
                ClientsRes.strings.clients_import_result_reason_missing_contact
            }

            RejectedClientImport.Reason.ALREADY_EXISTS -> {
                ClientsRes.strings.clients_import_result_reason_exists
            }

            RejectedClientImport.Reason.LIMIT_REACHED -> {
                ClientsRes.strings.clients_import_result_reason_limit
            }

            RejectedClientImport.Reason.UNKNOWN -> {
                ClientsRes.strings.clients_import_result_reason_unknown
            }
        }
    }

    private fun showContactsAccessDialog() {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = ClientsRes.strings.clients_import_permission_title.desc(),
                message = ClientsRes.strings.clients_import_permission_message.desc(),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                        actionType = ActionType.CANCEL,
                    ),
                    ButtonDescriptor(
                        text =
                        ClientsRes.strings.clients_import_permission_open_settings.desc(),
                        actionType = ActionType.POSITIVE,
                        onClick = weakVMClosure { it.device.openAppSettings() },
                    ),
                ),
            ),
        )
    }

    private fun onAddClientClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(AddClient(it)) },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        searchText.value = query
    }

    private fun ClientsListState.setup(): ClientsListState {
        appBar.title = ClientsRes.strings.clients_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(ClientsListDestination.Back)
        }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        contactsPicker.pickerTitle = ClientsRes.strings.clients_import_contacts_title.desc()
        contactsPicker.pickerType = PickerFieldState.PickerType.SCREEN
        contactsPicker.onItemsPicked = weakVMClosure { vm, picked -> vm.onContactsPicked(picked) }
        clientsList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ClientsRes.strings.clients_empty.desc(),
        )
        return this
    }

    private companion object {
        const val EDIT_ACCESS_KEY = "clients_list_edit_access"
        const val IMPORT_CONTACTS_KEY = "clients_list_import_contacts"
        const val MIN_SERVER_SEARCH_LENGTH = 2
        val SERVER_SEARCH_DELAY = 300.milliseconds
    }
}
