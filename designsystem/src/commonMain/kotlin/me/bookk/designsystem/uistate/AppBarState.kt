package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.presentation.error.ActionType

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
    val icon: ImageResource? = null,
    val contentDescription: StringDesc,
    val type: ActionType = ActionType.POSITIVE,
    val onClick: () -> Unit
)
