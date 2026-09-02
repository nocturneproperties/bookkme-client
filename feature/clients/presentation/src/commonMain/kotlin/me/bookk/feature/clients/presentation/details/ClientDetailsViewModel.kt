package me.bookk.feature.clients.presentation.details

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import library.device.api.DeviceFacade
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientEvent
import me.bookk.feature.clients.domain.api.entity.listenFor
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination.Back
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination.Edit
import kotlin.properties.Delegates.notNull
import kotlin.uuid.Uuid

class ClientDetailsViewModel(
    private val id: Uuid,
    private val getClient: GetClient,
    private val device: DeviceFacade,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ClientDetailsState = stateFactory.createClientDetailsState().setup()
    private var client: Client by notNull()

    init {
        loadClient()
        listenFor<ClientEvent.Updated>()
            .filter { it.client.id == id }
            .onEach { loadClient() }
            .launchIn(viewModelScope)
    }

    private fun loadClient() {
        launch(
            launchIn = Dispatchers.Default,
            call = { getClient(id) },
            onComplete = {
                client = it
                uiState.appBar.title = it.fullName.desc()
                uiState.infoSections.replace(
                    listOf(
                        InfoLine(
                            title = ClientsRes.strings.clients_create_phone,
                            value = it.phone,
                            onClick = { device.dial(it.phone) }
                        ),
                        InfoLine(
                            title = ClientsRes.strings.clients_create_email,
                            value = it.email.ifBlank { "-" },
                            onClick = { device.mail(it.email) }
                        ),
                        InfoLine(
                            title = ClientsRes.strings.clients_details_description,
                            value = it.description?.ifBlank { "-" } ?: "-"
                        )
                    )
                )
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun onEditClick() {
        uiState.navigation.push(Edit(id))
    }

    private fun ClientDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_edit.desc(),
                    onClick = weakVMClosure { it.onEditClick() }
                )
            )
        )
    }
}
