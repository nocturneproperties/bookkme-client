package me.bookk.feature.services.presentation.group.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidServiceGroupListState : ServiceGroupListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val search: TextFieldState = AndroidTextFieldState()
    override val groups: ListState<ServiceGroupListState.ServiceGroupUI> = AndroidListState()
    override val refreshState: RefreshState = AndroidRefreshState()
    override var isAddGroupDialogVisible: Boolean by mutableStateOf(false)
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<ServiceGroupListDestination> = AndroidNavigationState()
}
