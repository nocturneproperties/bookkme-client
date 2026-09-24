package me.bookk.feature.clients.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.clients.presentation.create.CreateClientDestination
import me.bookk.feature.clients.presentation.create.CreateClientState
import me.bookk.feature.clients.presentation.details.ClientDetailsDestination
import me.bookk.feature.clients.presentation.details.ClientDetailsState
import me.bookk.feature.clients.presentation.edit.EditClientDestination
import me.bookk.feature.clients.presentation.edit.EditClientState
import me.bookk.feature.clients.presentation.list.ClientSection
import me.bookk.feature.clients.presentation.list.ClientsListDestination
import me.bookk.feature.clients.presentation.list.ClientsListState

internal class FakeClientsStateFactory : ClientsStateFactory {
    override fun createClientsListState(): ClientsListState = FakeClientsListState()
    override fun createClientState(): CreateClientState = FakeCreateClientState()
    override fun createClientDetailsState(): ClientDetailsState = FakeClientDetailsState()
    override fun createEditClientState(): EditClientState = FakeEditClientState()
}

internal class FakeClientsListState : ClientsListState {
    override val appBar = FakeAppBarState()
    override val searchField = FakeTextFieldState()
    override val clientsList = FakeListState<ClientSection>()
    override val refreshState = FakeRefreshState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ClientsListDestination>()
}

internal class FakeCreateClientState : CreateClientState {
    override val appBar = FakeAppBarState()
    override val name = FakeTextFieldState()
    override val lastName = FakeTextFieldState()
    override val phone = FakeTextFieldState()
    override val email = FakeTextFieldState()
    override val submit = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<CreateClientDestination>()
}

internal class FakeClientDetailsState : ClientDetailsState {
    override val appBar = FakeAppBarState()
    override val infoSections = FakeListState<InfoLine>()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ClientDetailsDestination>()
}

internal class FakeEditClientState : EditClientState {
    override val appBar = FakeAppBarState()
    override var isAttachedInfoVisible: Boolean = false
    override var attachedInfoText: StringDesc = "".desc()
    override val name = FakeTextFieldState()
    override val lastName = FakeTextFieldState()
    override val phone = FakeTextFieldState()
    override val email = FakeTextFieldState()
    override val description = FakeTextFieldState()
    override val submit = FakeButtonState()
    override val deleteButton = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<EditClientDestination>()
}
