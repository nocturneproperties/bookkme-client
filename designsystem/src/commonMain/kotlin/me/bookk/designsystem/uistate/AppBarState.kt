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
    val actions: ListState<AppBarActionState>

    fun createAction(): AppBarActionState
}

interface AppBarActionState : ViewState {
    var icon: ImageResource?
    var contentDescription: StringDesc
    var type: ActionType
    var isEnabled: Boolean
    var isLoading: Boolean
    var onClick: () -> Unit
}

fun AppBarActionState.startLoading() {
    isLoading = true
    isEnabled = false
}

fun AppBarActionState.stopLoading(enable: Boolean = true) {
    isLoading = false
    isEnabled = enable
}

fun AppBarState.action(
    contentDescription: StringDesc,
    icon: ImageResource? = null,
    type: ActionType = ActionType.POSITIVE,
    onClick: () -> Unit
): AppBarActionState {
    return createAction().apply {
        this.contentDescription = contentDescription
        this.icon = icon
        this.type = type
        this.onClick = onClick
    }
}
