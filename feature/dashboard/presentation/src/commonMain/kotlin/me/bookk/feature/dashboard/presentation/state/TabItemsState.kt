package me.bookk.feature.dashboard.presentation.state

import dev.icerock.moko.resources.desc.StringDesc

interface TabItemsState {
    var selectedItemId: TabItem.Id
    val items: List<TabItem>

    class InitData(
        val selectedItemId: TabItem.Id,
        val tabInitData: List<TabItem.InitData>
    )
}

interface TabItem {
    val id: Id
    val text: StringDesc
    var badgeText: StringDesc?

    class InitData(
        val id: Id,
        val text: StringDesc,
        val badgeText: StringDesc? = null
    )
    enum class Id {
        HOME,
        BUSINESS,
        SETTINGS
    }
}