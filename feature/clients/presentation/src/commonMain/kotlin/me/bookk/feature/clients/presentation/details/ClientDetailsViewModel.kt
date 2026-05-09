package me.bookk.feature.clients.presentation.details

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import library.device.api.DeviceFacade
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
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
                            title = ClientsRes.strings.clients_create_phone.desc(),
                            value = it.phone.desc(),
                            onClick = { device.dial(it.phone) }
                        ),
                        InfoLine(
                            title = ClientsRes.strings.clients_create_email.desc(),
                            value = it.email.ifBlank { "-" }.desc(),
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
            PresentationNotification.Message(
                title = ClientsRes.strings.clients_delete_title.desc(),
                message = ClientsRes.strings.clients_delete_desc.desc(),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                        actionType = ButtonDescriptor.ActionType.POSITIVE
                    ),
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_confirm.desc(),
                        actionType = ButtonDescriptor.ActionType.NEGATIVE,
                        onClick = ::onDeleteConfirmed
                    )
                ),
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
        appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(Back) }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_delete.desc(),
                    onClick = weakSelfClosure { it.onDeleteClient() }
                )
            )
        )
    }
}