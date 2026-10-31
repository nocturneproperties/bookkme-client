package me.bookk.feature.clients.presentation.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidOptionsMultiPickerState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

internal class AndroidClientsListState : ClientsListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val searchField: TextFieldState = AndroidTextFieldState()
    override val clientsList: ListState<ClientSection> = AndroidListState()
    override val refreshState: RefreshState = AndroidRefreshState()
    override val contactsPicker: OptionsMultiPickerState<SimplePickerPresentation<ClientImportEntry>> =
        AndroidOptionsMultiPickerState()
    override var isContactsPickerVisible: Boolean by mutableStateOf(false)
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<ClientsListDestination> = AndroidNavigationState()
}