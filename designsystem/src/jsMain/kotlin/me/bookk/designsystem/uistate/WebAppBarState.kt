package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.error.ActionType
import me.bookk.designsystem.uistate.AppBarActionState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.TopBarSize

class WebAppBarState(
    title: StringDesc = "".desc(),
    subtitle: StringDesc? = null,
    isVisible: Boolean = true,
    size: TopBarSize = TopBarSize.SMALL,
    onBackClick: (() -> Unit)? = null,
) : WebViewState(isVisible),
    AppBarState {
    override var title: StringDesc by mutableStateOf(title)
    override var subtitle: StringDesc? by mutableStateOf(subtitle)
    override var onBackClick: (() -> Unit)? by mutableStateOf(onBackClick)
    override var size: TopBarSize by mutableStateOf(size)
    override val actions = WebListState<AppBarActionState>()

    override fun createAction(): AppBarActionState {
        return WebAppBarActionState()
    }
}

class WebAppBarActionState :
    WebViewState(),
    AppBarActionState {
    override var icon: ImageResource? by mutableStateOf(null)
    override var contentDescription: StringDesc by mutableStateOf("".desc())
    override var type: ActionType by mutableStateOf(ActionType.POSITIVE)
    override var isEnabled: Boolean by mutableStateOf(true)
    override var isLoading: Boolean by mutableStateOf(false)
    override var onClick: () -> Unit by mutableStateOf({})
}
