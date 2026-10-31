package me.bookk.feature.clients.presentation.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

interface ClientsListState {
    val appBar: AppBarState
    val searchField: TextFieldState
    val clientsList: ListState<ClientSection>
    val refreshState: RefreshState
    val contactsPicker: OptionsMultiPickerState<SimplePickerPresentation<ClientImportEntry>>
    var isContactsPickerVisible: Boolean

    val notifications: PresentationNotificationState
    val navigation: NavigationState<ClientsListDestination>
}