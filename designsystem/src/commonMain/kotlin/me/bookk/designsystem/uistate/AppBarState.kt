package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

enum class TopBarSize {
    SMALL,
    LARGE
}

interface AppBarState : ViewState {
    var title: StringDesc
    var subtitle: StringDesc?
    var onBackClick: (() -> Unit)?
    var size: TopBarSize
    val actions: ListState<AppBarAction>
}

class AppBarAction(
    val icon: ImageResource,
    val contentDescription: StringDesc?,
    val onClick: () -> Unit
)
