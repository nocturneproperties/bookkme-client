package me.bookk.feature.dashboard.presentation.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.dashboard.presentation.DashboardHomeNavigationDestination

class AndroidDashboardState(
    override val tabItems: TabItemsState,
    override val navigation: NavigationState<DashboardHomeNavigationDestination>,
    override val home: DashboardHomeState,
    override val notifications: PresentationNotificationState
) : DashboardState {
    constructor(initData: TabItemsState.InitData) : this(
        tabItems = AndroidTabItemState(initData),
        navigation = AndroidNavigationState(),
        home = AndroidDashboardHomeState(),
        notifications = AndroidNotificationState()
    )
}

class AndroidTabItemState(
    selectedItemId: TabItem.Id,
    override val items: List<TabItem>
) : TabItemsState {

    override var selectedItemId: TabItem.Id by mutableStateOf(selectedItemId)

    constructor(initData: TabItemsState.InitData) : this(
        selectedItemId = initData.selectedItemId,
        items = initData.tabInitData.map { AndroidTabItem(it) }
    )
}

class AndroidTabItem(data: TabItem.InitData) : TabItem {
    override val id: TabItem.Id = data.id
    override val text: StringDesc = data.text
    override var badgeText: StringDesc? by mutableStateOf(data.badgeText)
    override var isEnabled: Boolean by mutableStateOf(data.isEnabled)
}

class AndroidDashboardHomeState : DashboardHomeState {
    override var content: HomeContent by mutableStateOf(HomeContent.Loading)
    override val onboarding: OnboardingState = AndroidOnboardingState()
}

class AndroidOnboardingState : OnboardingState {
    override var isBusinessStepDone: Boolean by mutableStateOf(false)
    override var isPluginsStepUnlocked: Boolean by mutableStateOf(false)
    override var onCreateBusinessClick: (() -> Unit)? by mutableStateOf(null)
    override var onEnablePluginsClick: (() -> Unit)? by mutableStateOf(null)
}