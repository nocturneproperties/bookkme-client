package me.bookk.feature.clients.presentation.details

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import library.device.api.DeviceFacade
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.dashOnBlank
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination.Back
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination.Edit
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class ClientDetailsViewModel(
    @InjectedParam private val id: Uuid,
    private val getClient: GetClient,
    private val getClientsPermissions: GetClientsPermissions,
    private val device: DeviceFacade,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ClientDetailsState = stateFactory.createClientDetailsState().setup()

    init {
        observeClient()
    }

    private fun observeClient() {
        getClient.flow(id)
            .filterNotNull()
            .mapLatest { it to getClientsPermissions(it.businessId) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { (client, permissions) -> renderClient(client, permissions.canEdit) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderClient(client: Client, canEdit: Boolean) {
        renderEditAction(canEdit)
        uiState.appBar.title = client.fullName.desc()
        uiState.infoSections.replace(
            listOf(
                InfoLine(
                    title = ClientsRes.strings.clients_create_phone,
                    value = client.phone.dashOnBlank(),
                    onClick = { client.phone?.let { device.dial(it) } }
                ),
                InfoLine(
                    title = ClientsRes.strings.clients_create_email,
                    value = client.email.dashOnBlank(),
                    onClick = { client.email?.let { device.mail(it) } }
                ),
                InfoLine(
                    title = ClientsRes.strings.clients_details_description,
                    value = client.description.dashOnBlank()
                )
            )
        )
    }

    private fun renderEditAction(canEdit: Boolean) {
        val actions = if (canEdit) {
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_edit.desc(),
                    onClick = weakVMClosure { it.onEditClick() }
                )
            )
        } else {
            emptyList()
        }
        uiState.appBar.actions.replace(actions)
    }

    private fun onEditClick() {
        uiState.navigation.push(Edit(id))
    }

    private fun ClientDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
    }
}
