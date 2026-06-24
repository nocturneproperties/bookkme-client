package me.bookk.feature.clients.presentation.details

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import library.device.api.DeviceFacade
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination.Back
import kotlin.properties.Delegates.notNull
import kotlin.uuid.Uuid

class ClientDetailsViewModel(
    private val id: Uuid,
    private val getClient: GetClient,
    private val deleteClient: DeleteClient,
    private val device: DeviceFacade,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ClientDetailsState = stateFactory.createClientDetailsState().setup()
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
                        )
                    )
                )
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun onDeleteClient() {
        uiState.notifications.add(
            PresentationNotification.Message.deleteConfirmation(
                message = ClientsRes.strings.clients_delete_desc.desc(),
                onConfirmed = ::onDeleteConfirmed,
            )
        )
    }

    private fun onDeleteConfirmed() {
        launch(
            launchIn = Dispatchers.Default,
            call = { deleteClient(client) },
            onComplete = { uiState.navigation.push(Back) },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun ClientDetailsState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_delete.desc(),
                    type = ActionType.NEGATIVE,
                    onClick = weakVMClosure { it.onDeleteClient() }
                )
            )
        )
    }
}
