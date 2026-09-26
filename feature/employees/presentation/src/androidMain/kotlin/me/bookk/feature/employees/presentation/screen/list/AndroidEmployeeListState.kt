package me.bookk.feature.employees.presentation.screen.list

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

internal class AndroidEmployeeListState : EmployeeListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val searchField: TextFieldState = AndroidTextFieldState()
    override val employeesList: ListState<EmployeeSection> = AndroidListState()
    override val refreshState: RefreshState = AndroidRefreshState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EmployeeListDestinations> = AndroidNavigationState()
}
