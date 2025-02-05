package me.bookk.feature.dashboard.presentation.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.dashboard.presentation.DashboardNavigationDestination

class AndroidDashboardState(
    override val tabItems: TabItemsState,
    override val navigation: NavigationState<DashboardNavigationDestination>
) : DashboardState {
    constructor(initData: TabItemsState.InitData) : this(
        tabItems = AndroidTabItemState(initData),
        navigation = AndroidNavigationState()
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
}