package me.bookk.feature.employees.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.feature.employees.presentation.screen.invite.InvitationItem
import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeDestinations
import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeState
import me.bookk.feature.employees.presentation.screen.list.EmployeeListDestinations
import me.bookk.feature.employees.presentation.screen.list.EmployeeListState
import me.bookk.feature.employees.presentation.screen.list.EmployeeSection

internal class FakeEmployeesStateFactory : EmployeesStateFactory {
    override fun createEmployeeListState(): EmployeeListState = FakeEmployeeListState()
    override fun createInviteEmployeeState(): InviteEmployeeState = FakeInviteEmployeeState()
}

internal class FakeEmployeeListState : EmployeeListState {
    override val appBar = FakeAppBarState()
    override val searchField = FakeTextFieldState()
    override val employeesList = FakeListState<EmployeeSection>()
    override val refreshState = FakeRefreshState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<EmployeeListDestinations>()
}

internal class FakeInviteEmployeeState : InviteEmployeeState {
    override val appBar = FakeAppBarState()
    override var descriptionText: StringDesc = "".desc()
    override val generateCodeButton = FakeButtonState()
    override var invitationsHeader: StringDesc = "".desc()
    override val invitationsList = FakeListState<InvitationItem>()
    override val refreshState = FakeRefreshState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<InviteEmployeeDestinations>()
}
