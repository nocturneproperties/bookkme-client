package me.bookk.feature.employees.presentation.screen.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

interface EmployeeListState {
    val appBar: AppBarState
    val searchField: TextFieldState
    val employeesList: ListState<EmployeeSection>
    val refreshState: RefreshState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<EmployeeListDestinations>
}
