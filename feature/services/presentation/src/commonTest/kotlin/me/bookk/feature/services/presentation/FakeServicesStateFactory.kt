package me.bookk.feature.services.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeBooleanState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.FakeViewState
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.feature.services.presentation.group.add.AddGroupNavigation
import me.bookk.feature.services.presentation.group.add.AddGroupState
import me.bookk.feature.services.presentation.group.list.ServiceGroupListDestination
import me.bookk.feature.services.presentation.group.list.ServiceGroupListState
import me.bookk.feature.services.presentation.service.add.AddServiceDestination
import me.bookk.feature.services.presentation.service.add.AddServiceState
import me.bookk.feature.services.presentation.service.list.ServiceListDestination
import me.bookk.feature.services.presentation.service.list.ServiceListState

internal class FakeServicesStateFactory : ServicesStateFactory {
    override fun createServiceListState(): ServiceListState = FakeServiceListState()
    override fun createServiceState(): AddServiceState = FakeAddServiceState()
    override fun createServiceGroupListState(): ServiceGroupListState = FakeServiceGroupListState()
    override fun createAddGroupState(): AddGroupState = FakeAddGroupState()
}

internal class FakeServiceListState : ServiceListState {
    override val appBar = FakeAppBarState()
    override val searchField = FakeTextFieldState()
    override var groupsSection: Action = Action("".desc())
    override val refreshState = FakeRefreshState()
    override val services = FakeListState<ServiceListState.ServiceGroupUI>()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ServiceListDestination>()
}

internal class FakeAddServiceState : FakeViewState(), AddServiceState {
    override val appBar = FakeAppBarState()
    override val group = FakePickerFieldState<AddServiceState.GroupUI>()
    override val name = FakeTextFieldState()
    override val duration = FakeTextFieldState()
    override val price = FakeTextFieldState()
    override val enabled = FakeBooleanState()
    override val create = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AddServiceDestination>()
}

internal class FakeServiceGroupListState : ServiceGroupListState {
    override val appBar = FakeAppBarState()
    override val search = FakeTextFieldState()
    override val groups = FakeListState<ServiceGroupListState.ServiceGroupUI>()
    override val refreshState = FakeRefreshState()
    override var isAddGroupDialogVisible: Boolean = false
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ServiceGroupListDestination>()
}

internal class FakeAddGroupState : AddGroupState {
    override var title: StringDesc = "".desc()
    override val name = FakeTextFieldState()
    override val create = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AddGroupNavigation>()
}
